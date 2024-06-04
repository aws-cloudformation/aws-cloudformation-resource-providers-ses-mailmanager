package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionRequest;
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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete AddonSubscription ID %s", clientRequestToken, model.getAddonSubscriptionId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerAddonSubscription::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((deleteAddonSubscriptionRequest, _proxyClient) -> deleteResource(deleteAddonSubscriptionRequest, _proxyClient, clientRequestToken))
                                .handleError((deleteAddonSubscriptionRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteAddonSubscriptionResponse deleteResource(
            final DeleteAddonSubscriptionRequest deleteAddonSubscriptionRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] AddonSubscription ID <%s> is deleting.", clientRequestToken, deleteAddonSubscriptionRequest.addonSubscriptionId()));

        GetAddonSubscriptionRequest request = GetAddonSubscriptionRequest.builder()
                .addonSubscriptionId(deleteAddonSubscriptionRequest.addonSubscriptionId())
                .build();

        // Need to check if resource exists before deleting due to idempotent deletion.
        proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getAddonSubscription);

        return proxyClient.injectCredentialsAndInvokeV2(deleteAddonSubscriptionRequest, proxyClient.client()::deleteAddonSubscription);

    }
}
