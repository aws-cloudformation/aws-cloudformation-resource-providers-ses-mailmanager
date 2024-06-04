package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.GetAddonSubscriptionResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanageraddonsubscription.utils.TagsConvertor.convertFromSdk;

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

        return ProgressEvent.progress(model, callbackContext)
                .then(
                        progress -> proxy.initiate("AWS-SES-MailManagerAddonSubscription::Read", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall((getAddonSubscriptionRequest, _proxyClient)
                                        -> readResource(getAddonSubscriptionRequest, _proxyClient, clientRequestToken))
                                .handleError((getAddonSubscriptionRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((getAddonSubscriptionRequest, getAddonSubscriptionResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> ProgressEvent.progress(Translator.translateFromReadResponse(getAddonSubscriptionResponse, _resourceModel.getAddonSubscriptionId()), _callbackContext))
                ).then(
                        progress -> proxy.initiate("AWS-SES-MailManagerAddonSubscription::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
                                .makeServiceCall((listTagsForResourceRequest, _proxyClient)
                                        -> listTagsForResource(listTagsForResourceRequest, _proxyClient, progress.getResourceModel(), clientRequestToken))
                                .handleError((listTagsForResourceRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((listTagsForResourceRequest, listTagsForResourceResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> constructResourceModelFromResponse(listTagsForResourceResponse, _resourceModel))
                );

    }

    private GetAddonSubscriptionResponse readResource(
            final GetAddonSubscriptionRequest getAddonSubscriptionRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Getting AddonSubscription with ID <%s>", clientRequestToken, getAddonSubscriptionRequest.addonSubscriptionId()));
        return proxyClient.injectCredentialsAndInvokeV2(getAddonSubscriptionRequest, proxyClient.client()::getAddonSubscription);
    }

    private ListTagsForResourceResponse listTagsForResource(
            final ListTagsForResourceRequest listTagsForResourceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] List Tags of AddonSubscription with ID <%s>", clientRequestToken, model.getAddonSubscriptionId()));
        return proxyClient.injectCredentialsAndInvokeV2(listTagsForResourceRequest, proxyClient.client()::listTagsForResource);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final ListTagsForResourceResponse response,
            final ResourceModel model
    ) {
        // Register Tags into ResourceModel as we need to override its stale value
        model.setTags(convertFromSdk(response.tags()));
        return ProgressEvent.defaultSuccessHandler(model);
    }
}
