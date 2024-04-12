package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanagerarchive.utils.ArchiveGetHelper.getArchiveWithStateCheck;

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

        return proxy.initiate("AWS-SES-MailManagerArchive::Read", proxyClient, model, callbackContext)
                .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((getArchiveRequest, _proxyClient)
                        -> readResource(getArchiveRequest, _proxyClient, clientRequestToken))
                .handleError((getArchiveRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((getArchiveRequest, getArchiveResponse, _proxyClient, _resourceModel, _callbackContext)
                        -> constructResourceModelFromResponse(getArchiveResponse));
    }

    private GetArchiveResponse readResource(
            final GetArchiveRequest getArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get Archive with ID %s", clientRequestToken, getArchiveRequest.archiveId()));

        return getArchiveWithStateCheck(proxyClient, getArchiveRequest.archiveId());
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(final GetArchiveResponse getArchiveResponse) {
        return ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(getArchiveResponse));
    }
}
