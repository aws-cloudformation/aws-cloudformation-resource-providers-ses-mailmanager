package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.ses.mailmanagerruleset.utils.TagsConvertor.convertFromSdk;

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
                        progress -> proxy.initiate("AWS-SES-MailManagerRuleSet::Read", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToReadRequest)
                                .makeServiceCall((getRuleSetRequest, _proxyClient)
                                        -> readResource(getRuleSetRequest, _proxyClient, clientRequestToken))
                                .handleError((getRuleSetRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((getRuleSetRequest, getRuleSetResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> ProgressEvent.progress(Translator.translateFromReadResponse(getRuleSetResponse), _callbackContext))
                ).then(
                        progress -> proxy.initiate("AWS-SES-MailManagerRuleSet::ListTags", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToListTagsForResourceRequest)
                                .makeServiceCall((listTagsForResourceRequest, _proxyClient)
                                        -> listTagsForResource(listTagsForResourceRequest, _proxyClient, progress.getResourceModel(), clientRequestToken))
                                .handleError((listTagsForResourceRequest, _exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .done((listTagsForResourceRequest, listTagsForResourceResponse, _proxyClient, _resourceModel, _callbackContext)
                                        -> constructResourceModelFromResponse(listTagsForResourceResponse, _resourceModel))
                );
    }

    private GetRuleSetResponse readResource(
            final GetRuleSetRequest getRuleSetRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get RuleSet with ID %s", clientRequestToken, getRuleSetRequest.ruleSetId()));
        return proxyClient.injectCredentialsAndInvokeV2(getRuleSetRequest, proxyClient.client()::getRuleSet);
    }

    private ListTagsForResourceResponse listTagsForResource(
            final ListTagsForResourceRequest listTagsForResourceRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] List Tags of RuleSet with ID <%s>", clientRequestToken, model.getRuleSetId()));
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
