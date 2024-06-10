package software.amazon.ses.mailmanagertrafficpolicy;

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

        return proxy.initiate("AWS-SES-MailManagerTrafficPolicy::List", proxyClient, model, callbackContext)
                .translateToServiceRequest(_resourceModel -> Translator.translateToListRequest(request.getNextToken()))
                .makeServiceCall((listTrafficPolicyRequest, _proxyClient)
                        -> _proxyClient.injectCredentialsAndInvokeV2(listTrafficPolicyRequest, _proxyClient.client()::listTrafficPolicies))
                .handleError((listTrafficPolicyRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((listTrafficPolicyRequest, listTrafficPolicyResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .resourceModels(Translator.translateFromListResponse(listTrafficPolicyResponse))
                                .status(OperationStatus.SUCCESS)
                                .nextToken(listTrafficPolicyResponse.nextToken())
                                .build());
    }
}
