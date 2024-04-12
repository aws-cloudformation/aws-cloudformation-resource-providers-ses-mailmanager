package software.amazon.ses.mailmanagerarchive;


import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ArchiveState;
import software.amazon.awssdk.services.mailmanager.model.DeleteArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteArchiveResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanagerarchive.utils.ArchiveGetHelper.getArchiveWithStateCheck;

public class DeleteHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete Archive ID %s", clientRequestToken, model.getArchiveId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerArchive::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((deleteArchiveRequest, _proxyClient)
                                        -> deleteResource(deleteArchiveRequest, _proxyClient, clientRequestToken))
                                .stabilize((deleteArchiveRequest, deleteArchiveResponse, _proxyClient, _resourceModel, _context)
                                        -> stabilizedOnDelete(_proxyClient, _resourceModel))
                                .handleError((deleteArchiveRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteArchiveResponse deleteResource(
            final DeleteArchiveRequest deleteArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Archive ID %s is deleting.", clientRequestToken, deleteArchiveRequest.archiveId()));

        getArchiveWithStateCheck(proxyClient, deleteArchiveRequest.archiveId());

        return proxyClient.injectCredentialsAndInvokeV2(deleteArchiveRequest, proxyClient.client()::deleteArchive);
    }

    private boolean stabilizedOnDelete(
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model
    ) {
        final ArchiveState status = proxyClient.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(model), proxyClient.client()::getArchive).archiveState();

        return status.equals(ArchiveState.PENDING_DELETION);
    }
}
