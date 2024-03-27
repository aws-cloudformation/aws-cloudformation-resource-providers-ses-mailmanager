package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        final String clientRequestToken = request.getClientRequestToken();

        logger.log(String.format("[ClientRequestToken: %s] Trying to update RuleSet with ID <%s>", clientRequestToken, model.getRuleSetId()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerRuleSet::Update", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToUpdateRequest)
                                .makeServiceCall((updateRuleSetRequest, _proxyClient) ->
                                        updateResource(updateRuleSetRequest, _proxyClient, clientRequestToken)
                                )
                                .handleError((updateRuleSetRequest, exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress())
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private UpdateRuleSetResponse updateResource(
            final UpdateRuleSetRequest updateRuleSetRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] RuleSet ID %s is updating", clientRequestToken, updateRuleSetRequest.ruleSetId()));
        return proxyClient.injectCredentialsAndInvokeV2(updateRuleSetRequest, proxyClient.client()::updateRuleSet);
    }
}
