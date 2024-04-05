package software.amazon.ses.mailmanagerrelay;


import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.GetRelayRequest;
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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete Relay ID %s", clientRequestToken, model.getRelayId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerRelay::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((deleteRelayRequest, _proxyClient) -> deleteResource(deleteRelayRequest, _proxyClient, clientRequestToken))
                                .handleError((deleteRelayRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteRelayResponse deleteResource(
            final DeleteRelayRequest deleteRelayRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Relay ID <%s> is deleting.", clientRequestToken, deleteRelayRequest.relayId()));

        GetRelayRequest request = GetRelayRequest.builder()
                .relayId(deleteRelayRequest.relayId())
                .build();

        // Need to check if resource exists before deleting due to idempotent deletion.
        proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getRelay);

        return proxyClient.injectCredentialsAndInvokeV2(deleteRelayRequest, proxyClient.client()::deleteRelay);

    }
}
