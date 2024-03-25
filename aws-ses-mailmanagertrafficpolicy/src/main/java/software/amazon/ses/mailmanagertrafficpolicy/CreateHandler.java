package software.amazon.ses.mailmanagertrafficpolicy;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateTrafficPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;


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

        if (StringUtils.isNullOrEmpty(model.getTrafficPolicyName())) {
            final String trafficPolicyName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    clientRequestToken,
                    MAX_TRAFFIC_POLICY_NAME_LENGTH
            );
            model.setTrafficPolicyName(trafficPolicyName);
        }

        logger.log(String.format("[ClientRequestToken: %s] Trying to create TrafficPolicy name %s", clientRequestToken, model.getTrafficPolicyName()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerTrafficPolicy::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall(
                                        (createTrafficPolicyRequest, _proxyClient)
                                                -> createResource(createTrafficPolicyRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createTrafficPolicyRequest, exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateTrafficPolicyResponse createResource(
            final CreateTrafficPolicyRequest createTrafficPolicyRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] TrafficPolicy with name <%s> is creating", clientRequestToken, model.getTrafficPolicyName()));

        CreateTrafficPolicyResponse response = proxyClient.injectCredentialsAndInvokeV2(createTrafficPolicyRequest, proxyClient.client()::createTrafficPolicy);

        logger.log(String.format("[ClientRequestToken: %s] TrafficPolicy with name <%s> is created and ID has been assigned with <%s>", clientRequestToken, model.getTrafficPolicyName(), response.trafficPolicyId()));

        if (model.getTrafficPolicyId() == null) {
            model.setTrafficPolicyId(response.trafficPolicyId());
        }

        return response;
    }
}
