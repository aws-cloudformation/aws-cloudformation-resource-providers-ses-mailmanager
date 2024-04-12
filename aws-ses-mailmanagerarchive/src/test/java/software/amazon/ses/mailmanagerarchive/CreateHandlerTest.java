package software.amazon.ses.mailmanagerarchive;

import java.time.Duration;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.CreateArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.RetentionPeriod;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.ARCHIVE_KMS_ARN;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.ARCHIVE_NAME;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.CLIENT_REQUEST_TOKEN;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.LOGICAL_RESOURCE_ID;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.MESSAGE_RETENTION_PERIOD_DAYS;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.fakeCreateArchiveResponse;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.fakeGetArchiveResponse;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    MailManagerClient mailManagerClient;
    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private ProxyClient<MailManagerClient> proxyClient;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        mailManagerClient = mock(MailManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, mailManagerClient);
    }

    @AfterEach
    public void tear_down() {
        verify(mailManagerClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(mailManagerClient);
    }

    @Test
    public void handle_create_request_simple_success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .retention(ArchiveRetention.builder()
                        .retentionPeriod(RetentionPeriod.FIVE_YEARS.toString())
                        .build())
                .kmsKeyArn(ARCHIVE_KMS_ARN)
                .messageRetentionPeriodDays(MESSAGE_RETENTION_PERIOD_DAYS.doubleValue())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .build();

        when(mailManagerClient.createArchive(any(CreateArchiveRequest.class))).thenReturn(fakeCreateArchiveResponse());
        when(mailManagerClient.getArchive(any(GetArchiveRequest.class))).thenReturn(fakeGetArchiveResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getResourceModel().getKmsKeyArn()).isNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getArchiveId()).isNotNull();
        assertThat(response.getResourceModel().getArchiveName()).isNotNull();
        assertThat(response.getResourceModel().getArchiveState()).isNotNull();
        assertThat(response.getResourceModel().getArchiveArn()).isNotNull();
        assertThat(response.getResourceModel().getRetention()).isEqualTo(request.getDesiredResourceState().getRetention());
        assertThat(response.getResourceModel().getMessageRetentionPeriodDays()).isEqualTo(request.getDesiredResourceState().getMessageRetentionPeriodDays());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_create_request_simple_failure_due_to_duplicate_name() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .archiveName(ARCHIVE_NAME)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .build();

        when(mailManagerClient.createArchive(any(CreateArchiveRequest.class))).thenThrow(ConflictException.builder().message("Not found").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("Not found");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }
}
