package software.amazon.ses.mailmanagerrelay;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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

        return proxy.initiate("AWS-SES-MailManagerRelay::Read", proxyClient, model, callbackContext)
                .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((getRelayRequest, _proxyClient) ->
                        readResource(getRelayRequest, _proxyClient, clientRequestToken))
                .handleError((getRelayRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((getRelayRequest, getRelayResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        constructResourceModelFromResponse(getRelayResponse));
    }

    private GetRelayResponse readResource(
            final GetRelayRequest getRelayRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Getting Relay with ID <%s>", clientRequestToken, getRelayRequest.relayId()));
        return proxyClient.injectCredentialsAndInvokeV2(getRelayRequest, proxyClient.client()::getRelay);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final GetRelayResponse getRelayResponse) {
        return ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(getRelayResponse));
    }
}
