package software.amazon.ses.mailmanagerrelay;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateRelayResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;

import static software.amazon.ses.mailmanagerrelay.Translator.translateToCreateRequest;


public class CreateHandler extends BaseHandlerStd {
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

        if (StringUtils.isNullOrEmpty(model.getRelayName())) {
            final String relayName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    clientRequestToken,
                    MAX_RELAY_NAME_LENGTH
            );
            model.setRelayName(relayName);
        }

        logger.log(String.format("[ClientRequestToken: %s] Trying to create Relay with name <%s>", clientRequestToken, model.getRelayName()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerRelay::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(m -> translateToCreateRequest(model, request))
                                .makeServiceCall((createRelayRequest, _proxyClient)
                                        -> createResource(createRelayRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createRelayRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateRelayResponse createResource(
            final CreateRelayRequest createRelayRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Relay with name <%s> is creating", clientRequestToken, model.getRelayName()));

        CreateRelayResponse response = proxyClient.injectCredentialsAndInvokeV2(createRelayRequest, proxyClient.client()::createRelay);

        logger.log(String.format("[ClientRequestToken: %s] Relay with name <%s> is created and ID has been assigned with <%s>", clientRequestToken, model.getRelayName(), response.relayId()));

        if (model.getRelayId() == null) {
            model.setRelayId(response.relayId());
        }

        return response;
    }
}
