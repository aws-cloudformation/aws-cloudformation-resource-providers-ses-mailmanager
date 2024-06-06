package software.amazon.ses.mailmanageraddoninstance;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonInstanceRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonInstanceResponse;
import software.amazon.awssdk.services.mailmanager.model.GetAddonInstanceRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete AddonInstance ID %s", clientRequestToken, model.getAddonInstanceId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerAddonInstance::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((deleteAddonInstanceRequest, _proxyClient)
                                        -> deleteResource(deleteAddonInstanceRequest, _proxyClient, clientRequestToken))
                                .handleError((deleteAddonInstanceRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteAddonInstanceResponse deleteResource(
            final DeleteAddonInstanceRequest deleteAddonInstanceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] AddonInstance ID <%s> is deleting.", clientRequestToken, deleteAddonInstanceRequest.addonInstanceId()));

        GetAddonInstanceRequest request = GetAddonInstanceRequest.builder()
                .addonInstanceId(deleteAddonInstanceRequest.addonInstanceId())
                .build();

        // Need to check if resource exists before deleting due to idempotent deletion.
        proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getAddonInstance);

        return proxyClient.injectCredentialsAndInvokeV2(deleteAddonInstanceRequest, proxyClient.client()::deleteAddonInstance);

    }
}
