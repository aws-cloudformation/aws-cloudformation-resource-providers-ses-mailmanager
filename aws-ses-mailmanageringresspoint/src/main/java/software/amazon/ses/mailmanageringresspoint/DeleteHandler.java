package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete IngressPoint ID %s", clientRequestToken, model.getIngressPointId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerIngressPoint::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .backoffDelay(STABILIZATION_DELAY_DELETE)
                                .makeServiceCall((deleteIngressPointRequest, _proxyClient) -> deleteResource(deleteIngressPointRequest, _proxyClient, clientRequestToken))
                                .stabilize((deleteIngressPointRequest, deleteIngressPointResponse, _proxyClient, _resourceModel, _context) -> stabilizedOnDelete(_proxyClient, _resourceModel))
                                .handleError((deleteIngressPointRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteIngressPointResponse deleteResource(
            final DeleteIngressPointRequest deleteIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] IngressPoint ID %s is deleting.", clientRequestToken, deleteIngressPointRequest.ingressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(deleteIngressPointRequest, proxyClient.client()::deleteIngressPoint);
    }

    private boolean stabilizedOnDelete(
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model
    ) {

        final String ingressPointId = model.getIngressPointId();

        try {
            final String status = proxyClient.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(model), proxyClient.client()::getIngressPoint).statusAsString();

            switch (status) {
                case "DEPROVISIONING":
                    logger.log(String.format("IngressPoint %s is stabilizing, current state is %s", ingressPointId, status));
                    return false;
                default:
                    logger.log(String.format("IngressPoint %s reached unexpected state %s", ingressPointId, status));
                    throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, "IngressPointId");
            }
        } catch (ResourceNotFoundException e) {
            logger.log(String.format("IngressPoint <%s> is deleted", ingressPointId));
            return true;
        }
    }
}
