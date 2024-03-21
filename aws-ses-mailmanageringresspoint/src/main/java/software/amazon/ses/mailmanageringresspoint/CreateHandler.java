package software.amazon.ses.mailmanageringresspoint;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
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

        if (StringUtils.isNullOrEmpty(model.getIngressPointName())) {
            final String ingressPointName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    clientRequestToken,
                    MAX_TEMPLATE_NAME_LENGTH
            );
            model.setIngressPointName(ingressPointName);
        }

        logger.log(String.format("[ClientRequestToken: %s] Trying to create IngressPoint name <%s>", clientRequestToken, model.getIngressPointName()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerIngressPoint::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .backoffDelay(STABILIZATION_DELAY_CREATE)
                                .makeServiceCall(
                                        (createIngressPointRequest, _proxyClient)
                                                -> createResource(createIngressPointRequest, _proxyClient, clientRequestToken)
                                )
                                .stabilize(
                                        (createIngressPointRequest, createIngressPointResponse, _proxyClient, _resourceModel, _context)
                                                -> stabilizedOnCreate(createIngressPointResponse, _proxyClient, _resourceModel, clientRequestToken)
                                )
                                .handleError(
                                        (createIngressPointRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                                -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken)
                                )
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateIngressPointResponse createResource(
            final CreateIngressPointRequest createIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] IngressPoint is creating", clientRequestToken));
        return proxyClient.injectCredentialsAndInvokeV2(createIngressPointRequest, proxyClient.client()::createIngressPoint);
    }

    private boolean stabilizedOnCreate(
            final CreateIngressPointResponse response,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        if (model.getIngressPointId() == null) {
            model.setIngressPointId(response.ingressPointId());
        }

        final String ingressPointId = model.getIngressPointId();
        final IngressPointStatus status = proxyClient.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(model), proxyClient.client()::getIngressPoint).status();

        switch (status) {
            case ACTIVE:
                logger.log(String.format("[ClientRequestToken: %s] IngressPoint with ID <%s> is stabilized, current state is <%s>", clientRequestToken, ingressPointId, status));
                return true;
            case PROVISIONING:
                logger.log(String.format("[ClientRequestToken: %s] IngressPoint with ID <%s> is stabilizing, current state is <%s>", clientRequestToken, ingressPointId, status));
                return false;
            default:
                logger.log(String.format("[ClientRequestToken: %s] IngressPoint with ID <%s> reached unexpected state <%s>", clientRequestToken, ingressPointId, status));
                throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, ingressPointId);
        }
    }
}
