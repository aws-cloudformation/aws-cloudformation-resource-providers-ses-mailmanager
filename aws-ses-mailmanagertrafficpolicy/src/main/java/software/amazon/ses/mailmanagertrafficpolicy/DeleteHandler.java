package software.amazon.ses.mailmanagertrafficpolicy;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete TrafficPolicy ID %s", clientRequestToken, model.getTrafficPolicyId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(
                        progress ->
                                proxy.initiate("AWS-SES-MailManagerTrafficPolicy::Delete", proxyClient, model, callbackContext)
                                        .translateToServiceRequest(Translator::translateToDeleteRequest)
                                        .makeServiceCall((deleteTrafficPolicyRequest, _proxyClient)
                                                -> deleteResource(deleteTrafficPolicyRequest, _proxyClient, clientRequestToken))
                                        .handleError((deleteTrafficPolicyRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                                -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                        .progress()
                )
                .then(
                        progress -> ProgressEvent.defaultSuccessHandler(null)
                );
    }

    private DeleteTrafficPolicyResponse deleteResource(
            final DeleteTrafficPolicyRequest deleteTrafficPolicyRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] TrafficPolicy ID %s is deleting.", clientRequestToken, deleteTrafficPolicyRequest.trafficPolicyId()));

        GetTrafficPolicyRequest request = GetTrafficPolicyRequest.builder()
                .trafficPolicyId(deleteTrafficPolicyRequest.trafficPolicyId())
                .build();

        // Need to check if resource exists before deleting due to idempotent deletion.
        proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getTrafficPolicy);

        return proxyClient.injectCredentialsAndInvokeV2(deleteTrafficPolicyRequest, proxyClient.client()::deleteTrafficPolicy);
    }
}
