package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.DeleteRuleSetResponse;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
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

        logger.log(String.format("[ClientRequestToken: %s] Trying to delete RuleSet with ID <%s>", clientRequestToken, model.getRuleSetId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerRuleSet::Delete", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((deleteRuleSetRequest, _proxyClient) ->
                                        deleteResource(deleteRuleSetRequest, _proxyClient, clientRequestToken))
                                .handleError((deleteRuleSetRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteRuleSetResponse deleteResource(
            final DeleteRuleSetRequest deleteRuleSetRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] RuleSet with ID <%s> is deleting.", clientRequestToken, deleteRuleSetRequest.ruleSetId()));

        GetRuleSetRequest request = GetRuleSetRequest.builder()
                .ruleSetId(deleteRuleSetRequest.ruleSetId())
                .build();

        // Need to check if resource exists before deleting due to idempotent deletion.
        proxyClient.injectCredentialsAndInvokeV2(request, proxyClient.client()::getRuleSet);

        return proxyClient.injectCredentialsAndInvokeV2(deleteRuleSetRequest, proxyClient.client()::deleteRuleSet);
    }
}
