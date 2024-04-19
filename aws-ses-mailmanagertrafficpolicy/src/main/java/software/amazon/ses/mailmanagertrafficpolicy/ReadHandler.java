package software.amazon.ses.mailmanagertrafficpolicy;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanagertrafficpolicy.utils.TagsConvertor.convertFromSdk;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger
    ) {
        this.logger = logger;

        ResourceModel model = request.getDesiredResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        return ProgressEvent.progress(model, callbackContext)
                .then(
                        progress -> proxy.initiate("AWS-SES-MailManagerTrafficPolicy::Read", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall((getTrafficPolicyRequest, _proxyClient)
                                        -> readResource(getTrafficPolicyRequest, _proxyClient, clientRequestToken))
                                .handleError((getTrafficPolicyRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((getTrafficPolicyRequest, getTrafficPolicyResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> ProgressEvent.progress(Translator.translateFromReadResponse(getTrafficPolicyResponse), _callbackContext))
                ).then(
                        progress -> proxy.initiate("AWS-SES-MailManagerTrafficPolicy::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
                                .makeServiceCall((listTagsForResourceRequest, _proxyClient)
                                        -> listTagsForResource(listTagsForResourceRequest, _proxyClient, progress.getResourceModel(), clientRequestToken))
                                .handleError((listTagsForResourceRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((listTagsForResourceRequest, listTagsForResourceResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> constructResourceModelFromResponse(listTagsForResourceResponse, _resourceModel))
                );
    }

    private GetTrafficPolicyResponse readResource(
            final GetTrafficPolicyRequest getTrafficPolicyRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get TrafficPolicy with ID %s", clientRequestToken, getTrafficPolicyRequest.trafficPolicyId()));
        return proxyClient.injectCredentialsAndInvokeV2(getTrafficPolicyRequest, proxyClient.client()::getTrafficPolicy);
    }

    private ListTagsForResourceResponse listTagsForResource(
            final ListTagsForResourceRequest listTagsForResourceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] List Tags of TrafficPolicy with ID <%s>", clientRequestToken, model.getTrafficPolicyId()));
        return proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest, proxyClient.client()::listTagsForResource);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final ListTagsForResourceResponse response,
            final ResourceModel model
    ) {
        // Register Tags into ResourceModel as we need to override its stale value
        model.setTags(convertFromSdk(response.tags()));
        return ProgressEvent.defaultSuccessHandler(model);
    }
}
