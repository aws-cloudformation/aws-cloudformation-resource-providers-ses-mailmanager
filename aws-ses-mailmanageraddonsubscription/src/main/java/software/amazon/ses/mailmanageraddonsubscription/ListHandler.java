package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<MailManagerClient> proxyClient,
            final Logger logger
    ) {

        final ResourceModel model = request.getDesiredResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        return proxy.initiate("AWS-SES-MailManagerAddonSubscription::List", proxyClient, model, callbackContext)
                .translateToServiceRequest(_resourceModel ->
                        Translator.translateToListRequest(request.getNextToken()))
                .makeServiceCall((listAddonSubscriptionRequest, _proxyClient) ->
                        _proxyClient.injectCredentialsAndInvokeV2(listAddonSubscriptionRequest, _proxyClient.client()::listAddonSubscriptions))
                .handleError((listAddonSubscriptionRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((listAddonSubscriptionRequest, listAddonSubscriptionResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .resourceModels(Translator.translateFromListResponse(listAddonSubscriptionResponse))
                                .status(OperationStatus.SUCCESS)
                                .nextToken(listAddonSubscriptionResponse.nextToken())
                                .build());
    }
}
