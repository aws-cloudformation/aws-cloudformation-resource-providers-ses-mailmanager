package software.amazon.ses.mailmanagerruleset;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateRuleSetResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.resource.IdentifierUtils;


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

        if (StringUtils.isNullOrEmpty(model.getRuleSetName())) {
            final String ruleSetName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    clientRequestToken,
                    MAX_RULE_SET_NAME_LENGTH
            );
            model.setRuleSetName(ruleSetName);
        }

        logger.log(String.format("[ClientRequestToken: %s] Trying to create RuleSet name %s", clientRequestToken, model.getRuleSetName()));

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerRuleSet::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall((createRuleSetRequest, _proxyClient)
                                        -> createResource(createRuleSetRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createRuleSetRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateRuleSetResponse createResource(
            final CreateRuleSetRequest createRuleSetRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] RuleSet is creating", clientRequestToken));

        CreateRuleSetResponse response = proxyClient.injectCredentialsAndInvokeV2(createRuleSetRequest, proxyClient.client()::createRuleSet);

        logger.log(String.format("[ClientRequestToken: %s] RuleSet with ID <%s> is created", clientRequestToken, response.ruleSetId()));

        if (model.getRuleSetId() == null) {
            model.setRuleSetId(response.ruleSetId());
        }

        return response;
    }
}
