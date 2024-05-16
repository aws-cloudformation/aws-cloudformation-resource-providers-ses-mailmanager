package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointResponse;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import java.util.Set;

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
        final ResourceModel previousModel = request.getPreviousResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to update IngressPoint with ID <%s>", clientRequestToken, model.getIngressPointId()));

        if (!previousModel.getType().equals(model.getType())) {
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, model.getType());
        }

        ProgressEvent<ResourceModel, CallbackContext> progressEvent = ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerIngressPoint::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .backoffDelay(STABILIZATION_DELAY_UPDATE)
                                .makeServiceCall((updateIngressPointRequest, _proxyClient)
                                        -> updateResource(updateIngressPointRequest, _proxyClient, clientRequestToken)
                                )
                                .stabilize((updateIngressPointRequest, updateIngressPointResponse, _proxyClient, _resourceModel, _context)
                                        -> stabilizedOnUpdate(_proxyClient, _resourceModel))
                                .handleError((updateIngressPointRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                );

        if (TagHelper.shouldUpdateTags(request)) {
            final Map<String, String> previousTags = TagHelper.getPreviouslyAttachedTags(request);
            final Map<String, String> desiredTags = TagHelper.getNewDesiredTags(request);
            final Map<String, String> addedTags = TagHelper.generateTagsToAdd(previousTags, desiredTags);
            final Set<String> removedTags = TagHelper.generateTagsToRemove(previousTags, desiredTags);

            registerResourceArn(model, proxyClient);

            progressEvent = progressEvent
                    .then(
                            progress -> untagResource(
                                    proxy,
                                    proxyClient,
                                    model,
                                    request,
                                    callbackContext,
                                    clientRequestToken,
                                    removedTags,
                                    logger
                            ))
                    .then(
                            progress -> tagResource(
                                    proxy,
                                    proxyClient,
                                    model,
                                    request,
                                    callbackContext,
                                    clientRequestToken,
                                    addedTags,
                                    logger
                            ));
        }
        return progressEvent.then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateIngressPointResponse updateResource(
            final UpdateIngressPointRequest updateIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] IngressPoint ID <%s> is updating", clientRequestToken, updateIngressPointRequest.ingressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(updateIngressPointRequest, proxyClient.client()::updateIngressPoint);
    }

    private boolean stabilizedOnUpdate(
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model
    ) {
        final String ingressPointId = model.getIngressPointId();
        final IngressPointStatus status = proxyClient.injectCredentialsAndInvokeV2(
                Translator.translateToReadRequest(model),
                proxyClient.client()::getIngressPoint
        ).status();

        switch (status) {
            case ACTIVE:
            case CLOSED:
                logger.log(String.format("IngressPoint ID <%s> is stabilized, current state is %s", ingressPointId, status));
                return true;
            case UPDATING:
                logger.log(String.format("IngressPoint ID <%s> is stabilizing, current state is %s", ingressPointId, status));
                return false;
            default:
                logger.log(String.format("IngressPoint ID <%s> reached unexpected state %s", ingressPointId, status));
                throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, ingressPointId);
        }
    }

    /**
     * ReadOnlyProperties cannot be retrieved from neither DesiredResourceState nor PreviousResourceState
     * So we need to explicitly invoke GET API to get the resource's ARN
     */
    private void registerResourceArn(
            ResourceModel model,
            ProxyClient<MailManagerClient> proxyClient
    ) {
        if (model.getIngressPointArn() == null) {
            GetIngressPointRequest request = GetIngressPointRequest.builder()
                    .ingressPointId(model.getIngressPointId())
                    .build();

            GetIngressPointResponse res = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getIngressPoint);

            model.setIngressPointArn(res.ingressPointArn());
        }
    }

    /**
     * tagResource during update
     * <p>
     * Calls the service:TagResource API.
     */
    private ProgressEvent<ResourceModel, CallbackContext> tagResource(
            final AmazonWebServicesClientProxy proxy,
            final ProxyClient<MailManagerClient> serviceClient,
            final ResourceModel resourceModel,
            final ResourceHandlerRequest<ResourceModel> handlerRequest,
            final CallbackContext callbackContext,
            final String clientRequestToken,
            final Map<String, String> addedTags,
            final Logger logger
    ) {
        if (addedTags.isEmpty()) {
            return ProgressEvent.progress(resourceModel, callbackContext);
        }

        logger.log(String.format("[ClientRequestToken: %s] Going to add tags for IngressPoint with ID <%s> under AccountId: <%s>",
                clientRequestToken, resourceModel.getIngressPointId(), handlerRequest.getAwsAccountId()));

        return proxy.initiate("AWS-SES-MailManagerIngressPoint::TagResource", serviceClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> Translator.tagResourceRequest(model, addedTags))
                .makeServiceCall((request, client)
                        -> proxy.injectCredentialsAndInvokeV2(request, client.client()::tagResource))
                .handleError((tagResourceRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                        -> handleError(exception, resourceModel, callbackContext, logger, clientRequestToken))
                .progress();
    }

    /**
     * untagResource during update
     * <p>
     * Calls the service:UntagResource API.
     */
    private ProgressEvent<ResourceModel, CallbackContext> untagResource(
            final AmazonWebServicesClientProxy proxy,
            final ProxyClient<MailManagerClient> serviceClient,
            final ResourceModel resourceModel,
            final ResourceHandlerRequest<ResourceModel> handlerRequest,
            final CallbackContext callbackContext,
            final String clientRequestToken,
            final Set<String> removedTags,
            final Logger logger
    ) {
        if (removedTags.isEmpty()) {
            return ProgressEvent.progress(resourceModel, callbackContext);
        }

        logger.log(String.format("[ClientRequestToken: %s] Going to remove tags for IngressPoint with ID <%s> under AccountId: <%s>",
                clientRequestToken, resourceModel.getIngressPointId(), handlerRequest.getAwsAccountId()));

        return proxy.initiate("AWS-SES-MailManagerIngressPoint::UntagResource", serviceClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> Translator.untagResourceRequest(model, removedTags))
                .makeServiceCall((request, client)
                        -> proxy.injectCredentialsAndInvokeV2(request, client.client()::untagResource))
                .handleError((untagResourceRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                        -> handleError(exception, resourceModel, callbackContext, logger, clientRequestToken))
                .progress();
    }
}
