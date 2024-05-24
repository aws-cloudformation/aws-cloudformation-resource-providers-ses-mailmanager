package software.amazon.ses.mailmanagerarchive;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveResponse;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static software.amazon.ses.mailmanagerarchive.utils.ArchiveGetHelper.getArchiveWithStateCheck;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to update Archive ID %s", clientRequestToken, model.getArchiveId()));

        // KMS key used for archiving cannot be updated once resource gets created
        if (!Objects.equals(previousModel.getKmsKeyArn(), model.getKmsKeyArn())) {
            throw new CfnNotUpdatableException(ResourceModel.TYPE_NAME, model.getArchiveId());
        }

        ProgressEvent<ResourceModel, CallbackContext> progressEvent = ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerArchive::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .makeServiceCall((updateArchiveRequest, _proxyClient)
                                        -> updateResource(updateArchiveRequest, _proxyClient, clientRequestToken)
                                )
                                .handleError((updateArchiveRequest, exception, _proxyClient, _resourceModel, _callbackContext)
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

    private UpdateArchiveResponse updateResource(
            final UpdateArchiveRequest updateArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Archive ID %s is updating", clientRequestToken, updateArchiveRequest.archiveId()));

        getArchiveWithStateCheck(proxyClient, updateArchiveRequest.archiveId());

        return proxyClient.injectCredentialsAndInvokeV2(updateArchiveRequest, proxyClient.client()::updateArchive);
    }

    /**
     * ReadOnlyProperties cannot be retrieved from neither DesiredResourceState nor PreviousResourceState
     * So we need to explicitly invoke GET API to get the resource's ARN
     */
    private void registerResourceArn(
            ResourceModel model,
            ProxyClient<MailManagerClient> proxyClient
    ) {
        if (model.getArchiveArn() == null) {
            GetArchiveRequest request = GetArchiveRequest.builder()
                    .archiveId(model.getArchiveId())
                    .build();

            GetArchiveResponse res = proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getArchive);

            model.setArchiveArn(res.archiveArn());
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

        logger.log(String.format("[ClientRequestToken: %s] Going to add tags for Archive with ID <%s> under AccountId: <%s>",
                clientRequestToken, resourceModel.getArchiveId(), handlerRequest.getAwsAccountId()));

        return proxy.initiate("AWS-SES-MailManagerArchive::TagResource", serviceClient, resourceModel, callbackContext)
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

        logger.log(String.format("[ClientRequestToken: %s] Going to remove tags for Archive with ID <%s> under AccountId: <%s>",
                clientRequestToken, resourceModel.getArchiveId(), handlerRequest.getAwsAccountId()));

        return proxy.initiate("AWS-SES-MailManagerArchive::UntagResource", serviceClient, resourceModel, callbackContext)
                .translateToServiceRequest(model -> Translator.untagResourceRequest(model, removedTags))
                .makeServiceCall((request, client)
                        -> proxy.injectCredentialsAndInvokeV2(request, client.client()::untagResource))
                .handleError((untagResourceRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                        -> handleError(exception, resourceModel, callbackContext, logger, clientRequestToken))
                .progress();
    }
}
