package software.amazon.ses.mailmanageraddonsubscription;


import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateAddonSubscriptionRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateAddonSubscriptionResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanageraddonsubscription.Translator.translateToCreateRequest;


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

        logger.log(String.format("[ClientRequestToken: %s] Trying to create AddonSubscription with name <%s>", clientRequestToken, model.getAddonName()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerAddonSubscription::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(m -> translateToCreateRequest(model, request))
                                .makeServiceCall((createAddonSubscriptionRequest, _proxyClient)
                                        -> createResource(createAddonSubscriptionRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createAddonSubscriptionRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateAddonSubscriptionResponse createResource(
            final CreateAddonSubscriptionRequest createAddonSubscriptionRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] AddonSubscription with name <%s> is creating", clientRequestToken, model.getAddonName()));

        CreateAddonSubscriptionResponse response = proxyClient.injectCredentialsAndInvokeV2(createAddonSubscriptionRequest, proxyClient.client()::createAddonSubscription);

        logger.log(String.format("[ClientRequestToken: %s] AddonSubscription with name <%s> is created and ID has been assigned with <%s>", clientRequestToken, model.getAddonName(), response.addonSubscriptionId()));

        if (model.getAddonSubscriptionId() == null) {
            model.setAddonSubscriptionId(response.addonSubscriptionId());
        }

        return response;
    }
}
