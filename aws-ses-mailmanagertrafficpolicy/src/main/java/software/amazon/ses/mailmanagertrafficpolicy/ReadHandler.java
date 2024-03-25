package software.amazon.ses.mailmanagertrafficpolicy;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyResponse;
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

        return proxy.initiate("AWS-SES-MailManagerTrafficPolicy::Read", proxyClient, model, callbackContext)
                .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((getTrafficPolicyRequest, _proxyClient) -> readResource(getTrafficPolicyRequest, _proxyClient, clientRequestToken))
                .handleError((getTrafficPolicyRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((getTrafficPolicyRequest, getTrafficPolicyResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        constructResourceModelFromResponse(getTrafficPolicyResponse));
    }

    private GetTrafficPolicyResponse readResource(
            final GetTrafficPolicyRequest getTrafficPolicyRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get TrafficPolicy with ID %s", clientRequestToken, getTrafficPolicyRequest.trafficPolicyId()));
        return proxyClient.injectCredentialsAndInvokeV2(getTrafficPolicyRequest, proxyClient.client()::getTrafficPolicy);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final GetTrafficPolicyResponse getTrafficPolicyResponse) {
        return ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(getTrafficPolicyResponse));
    }
}
