package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;

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

        return proxy.initiate("AWS-SES-MailManagerIngressPoint::Read", proxyClient, model, callbackContext)
                .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((getIngressPointRequest, _proxyClient) -> readResource(getIngressPointRequest, _proxyClient, clientRequestToken))
                .handleError((getIngressPointRequest, _exception, _proxyClient, _resourceModel, _callbackContext) ->
                        handleError(_exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                .done((getIngressPointRequest, getIngressPointResponse, _proxyClient, _resourceModel, _callbackContext) ->
                        constructResourceModelFromResponse(getIngressPointResponse));
    }

    private GetIngressPointResponse readResource(
            final GetIngressPointRequest getIngressPointRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Get IngressPoint with ID %s", clientRequestToken, getIngressPointRequest.ingressPointId()));
        return proxyClient.injectCredentialsAndInvokeV2(getIngressPointRequest, proxyClient.client()::getIngressPoint);
    }

    private ProgressEvent<ResourceModel, CallbackContext> constructResourceModelFromResponse(
            final GetIngressPointResponse getIngressPointResponse) {

        return ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(getIngressPointResponse));
    }
}
