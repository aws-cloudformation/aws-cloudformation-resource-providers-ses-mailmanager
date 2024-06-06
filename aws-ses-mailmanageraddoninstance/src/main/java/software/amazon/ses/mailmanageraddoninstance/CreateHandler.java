package software.amazon.ses.mailmanageraddoninstance;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateAddonInstanceRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateAddonInstanceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanageraddoninstance.Translator.translateToCreateRequest;


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

        logger.log(String.format("[ClientRequestToken: %s] Trying to create AddonInstance with AddonSubscription <%s>", clientRequestToken, model.getAddonSubscriptionId()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerAddonInstance::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(m -> translateToCreateRequest(model, request))
                                .makeServiceCall((createAddonInstanceRequest, _proxyClient)
                                        -> createResource(createAddonInstanceRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createAddonInstanceRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateAddonInstanceResponse createResource(
            final CreateAddonInstanceRequest createAddonInstanceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] AddonInstance with AddonSubscription <%s> is creating", clientRequestToken, model.getAddonSubscriptionId()));

        CreateAddonInstanceResponse response = proxyClient.injectCredentialsAndInvokeV2(createAddonInstanceRequest, proxyClient.client()::createAddonInstance);

        logger.log(String.format("[ClientRequestToken: %s] AddonInstance with AddonSubscription <%s> is created and ID has been assigned with <%s>", clientRequestToken, model.getAddonSubscriptionId(), response.addonInstanceId()));

        if (model.getAddonInstanceId() == null) {
            model.setAddonInstanceId(response.addonInstanceId());
        }

        return response;
    }
}
