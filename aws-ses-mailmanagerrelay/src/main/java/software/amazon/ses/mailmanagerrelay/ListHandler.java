package software.amazon.ses.mailmanagerrelay;

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

        return proxy.initiate("AWS-SES-MailManagerRelay::List", proxyClient, model, callbackContext)
                .translateToServiceRequest(_resourceModel ->
                        Translator.translateToListRequest(request.getNextToken()))
                .makeServiceCall((listRelayRequest, _proxyClient) ->
                        _proxyClient.injectCredentialsAndInvokeV2(listRelayRequest, _proxyClient.client()::listRelays))
                .handleError((listRelayRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((listRelayRequest, listRelayResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .resourceModels(Translator.translateFromListResponse(listRelayResponse))
                                .status(OperationStatus.SUCCESS)
                                .nextToken(listRelayResponse.nextToken())
                                .build());
    }
}
