package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveResponse;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Objects;

import static software.amazon.ses.mailmanagerarchive.utils.ArchiveGetHelper.getArchiveWithStateCheck;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to update Archive ID %s", clientRequestToken, model.getArchiveId()));

        // KMS key used for archiving cannot be updated once resource gets created
        if (!Objects.equals(previousModel.getKmsKeyArn(), model.getKmsKeyArn())) {
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, model.getArchiveId());
        }

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerArchive::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .makeServiceCall((updateArchiveRequest, _proxyClient)
                                        -> updateResource(updateArchiveRequest, _proxyClient, clientRequestToken)
                                )
                                .handleError((updateArchiveRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress())
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateArchiveResponse updateResource(
            final UpdateArchiveRequest updateArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Archive ID %s is updating", clientRequestToken, updateArchiveRequest.archiveId()));

        getArchiveWithStateCheck(proxyClient, updateArchiveRequest.archiveId());

        return proxyClient.injectCredentialsAndInvokeV2(updateArchiveRequest, proxyClient.client()::updateArchive);
    }
}
