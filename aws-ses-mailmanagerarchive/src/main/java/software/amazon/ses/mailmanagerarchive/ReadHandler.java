package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanagerarchive.utils.ArchiveGetHelper.getArchiveWithStateCheck;
import static software.amazon.ses.mailmanagerarchive.utils.TagsConvertor.convertFromSdk;

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
                        progress ->  proxy.initiate("AWS-SES-MailManagerArchive::Read", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall((getArchiveRequest, _proxyClient)
                                        -> readResource(getArchiveRequest, _proxyClient, clientRequestToken))
                                .handleError((getArchiveRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((getArchiveRequest, getArchiveResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> ProgressEvent.progress(Translator.translateFromReadResponse(getArchiveResponse), _callbackContext))
                ).then(
                        progress -> proxy.initiate("AWS-SES-MailManagerArchive::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
                                .makeServiceCall((listTagsForResourceRequest, _proxyClient)
                                        -> listTagsForResource(listTagsForResourceRequest, _proxyClient, progress.getResourceModel(), clientRequestToken))
                                .handleError((listTagsForResourceRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((listTagsForResourceRequest, listTagsForResourceResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> constructResourceModelFromResponse(listTagsForResourceResponse, _resourceModel))
                );

    }

    private GetArchiveResponse readResource(
            final GetArchiveRequest getArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get Archive with ID %s", clientRequestToken, getArchiveRequest.archiveId()));

        return getArchiveWithStateCheck(proxyClient, getArchiveRequest.archiveId());
    }

    private ListTagsForResourceResponse listTagsForResource(
            final ListTagsForResourceRequest listTagsForResourceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] List Tags of Archive with ID <%s>", clientRequestToken, model.getArchiveId()));
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
