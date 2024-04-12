package software.amazon.ses.mailmanagerarchive;


import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.CreateArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.CreateArchiveResponse;
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
            final Logger logger
    ) {
        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final String clientRequestToken = request.getClientRequestToken();

        if (StringUtils.isNullOrEmpty(model.getArchiveName())) {
            final String archiveName = IdentifierUtils.generateResourceIdentifier(
                    request.getLogicalResourceIdentifier(),
                    clientRequestToken,
                    MAX_ARCHIVE_NAME_LENGTH
            );
            model.setArchiveName(archiveName);
        }

        return ProgressEvent.progress(model, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-SES-MailManagerArchive::Create", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToCreateRequest)
                                .makeServiceCall((createArchiveRequest, _proxyClient)
                                        -> createResource(createArchiveRequest, _proxyClient, model, clientRequestToken))
                                .handleError((createArchiveRequest, exception, _proxyClient, _resourceModel, _callbackContext)
                                        -> handleError(exception, _resourceModel, _callbackContext, logger, clientRequestToken))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateArchiveResponse createResource(
            final CreateArchiveRequest createArchiveRequest,
            final ProxyClient<MailManagerClient> proxyClient,
            ResourceModel model,
            final String clientRequestToken
    ) {
        logger.log(String.format("[ClientRequestToken: %s] Archive with name <%s> is creating", clientRequestToken, model.getArchiveName()));

        CreateArchiveResponse response = proxyClient.injectCredentialsAndInvokeV2(createArchiveRequest, proxyClient.client()::createArchive);

        logger.log(String.format("[ClientRequestToken: %s] Archive with name <%s> is created and ID has been assigned with <%s>", clientRequestToken, model.getArchiveName(), response.archiveId()));

        // Assign archiveId as primaryIdentifier
        model.setArchiveId(response.archiveId());

        return response;
    }
}
