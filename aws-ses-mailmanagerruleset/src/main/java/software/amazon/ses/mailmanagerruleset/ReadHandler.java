package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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

        return proxy.initiate("AWS-SES-MailManagerRuleSet::Read", proxyClient, model, callbackContext)
                .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((getRuleSetRequest, _proxyClient) ->
                        readResource(getRuleSetRequest, _proxyClient, clientRequestToken))
                .handleError((getRuleSetRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((getRuleSetRequest, getRuleSetResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        constructResourceModelFromResponse(getRuleSetResponse));
    }

    private GetRuleSetResponse readResource(
            final GetRuleSetRequest getRuleSetRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get RuleSet with ID %s", clientRequestToken, getRuleSetRequest.ruleSetId()));
        return proxyClient.injectCredentialsAndInvokeV2(getRuleSetRequest, proxyClient.client()::getRuleSet);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final GetRuleSetResponse getRuleSetResponse) {
        return ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(getRuleSetResponse));
    }
}
