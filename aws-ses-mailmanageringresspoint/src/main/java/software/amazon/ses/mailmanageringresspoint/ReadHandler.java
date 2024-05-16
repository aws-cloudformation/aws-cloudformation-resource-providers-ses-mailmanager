package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;

import static software.amazon.ses.mailmanageringresspoint.utils.TagsConvertor.convertFromSdk;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        ResourceModel model = request.getDesiredResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        return ProgressEvent.progress(model, callbackContext)
                .then(
                        progress -> proxy.initiate("AWS-SES-MailManagerIngressPoint::Read", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall((getIngressPointRequest, _proxyClient)
                                        -> readResource(getIngressPointRequest, _proxyClient, clientRequestToken))
                                .handleError((getIngressPointRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((getIngressPointRequest, getIngressPointResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> ProgressEvent.progress(Translator.translateFromReadResponse(getIngressPointResponse), _callbackContext))
                ).then(
                        progress -> proxy.initiate("AWS-SES-MailManagerIngressPoint::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
                                .makeServiceCall((listTagsForResourceRequest, _proxyClient)
                                        -> listTagsForResource(listTagsForResourceRequest, _proxyClient, progress.getResourceModel(), clientRequestToken))
                                .handleError((listTagsForResourceRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((listTagsForResourceRequest, listTagsForResourceResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> constructResourceModelFromResponse(listTagsForResourceResponse, _resourceModel))
                );
    }

    private GetIngressPointResponse readResource(
            final GetIngressPointRequest getIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get IngressPoint with ID %s", clientRequestToken, getIngressPointRequest.ingressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(getIngressPointRequest, proxyClient.client()::getIngressPoint);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final ListTagsForResourceResponse response,
            final ResourceModel model
    ) {
        // Register Tags into ResourceModel as we need to override its stale value
        model.setTags(convertFromSdk(response.tags()));
        return ProgressEvent.defaultSuccessHandler(model);
    }

    private ListTagsForResourceResponse listTagsForResource(
            final ListTagsForResourceRequest listTagsForResourceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] List Tags of IngressPoint with ID <%s>", clientRequestToken, model.getIngressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest, proxyClient.client()::listTagsForResource);
    }
}
