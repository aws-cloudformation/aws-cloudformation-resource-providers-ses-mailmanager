package software.amazon.ses.mailmanageringresspoint;

import lombok.NonNull;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointResponse;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Objects;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger
    ) {
        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final ResourceModel previous_model = request.getPreviousResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to update IngressPoint ID %s", clientRequestToken, model.getIngressPointId()));

        requestValidator(previous_model, model, clientRequestToken);

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerIngressPoint::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .backoffDelay(STABILIZATION_DELAY_UPDATE)
                                .makeServiceCall((updateIngressPointRequest, _proxyClient) ->
                                        updateResource(updateIngressPointRequest, _proxyClient, clientRequestToken)
                                )
                                .stabilize((updateIngressPointRequest, updateIngressPointResponse, _proxyClient, _resourceModel, _context) -> stabilizedOnUpdate(_proxyClient, _resourceModel, _context))
                                .handleError((updateIngressPointRequest, exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress())

                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateIngressPointResponse updateResource(
            final UpdateIngressPointRequest updateIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] IngressPoint ID %s is updating", clientRequestToken, updateIngressPointRequest.ingressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(updateIngressPointRequest, proxyClient.client()::updateIngressPoint);
    }

    private boolean stabilizedOnUpdate(
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final CallbackContext callbackContext
    ) {
        final String ingressPointId = model.getIngressPointId();
        final String status = proxyClient.injectCredentialsAndInvokeV2(
                Translator.translateToReadRequest(model),
                proxyClient.client()::getIngressPoint
        ).statusAsString();

        switch (status) {
            case "ACTIVE":
            case "CLOSED":
                logger.log(String.format("IngressPoint %s is stabilized, current state is %s", ingressPointId, status));
                return true;
            case "UPDATING":
                logger.log(String.format("IngressPoint %s is stabilizing, current state is %s", ingressPointId, status));
                return false;
            default:
                logger.log(String.format("IngressPoint %s reached unexpected state %s", ingressPointId, status));
                throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, "IngressPointId");
        }
    }

    private void requestValidator(@NonNull ResourceModel previous_model, @NonNull ResourceModel current_model, String clientRequestToken) {
        if (!Objects.equals(current_model.getIngressPointName(), previous_model.getIngressPointName())) {
            logger.log(String.format("[ClientRequestToken: %s] Unable to update createOnly property [IngressPointName]", clientRequestToken));
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, "IngressPointName");
        }
    }
}
