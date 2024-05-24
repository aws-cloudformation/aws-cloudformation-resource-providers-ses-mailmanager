package software.amazon.ses.mailmanagerarchive;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetArchiveRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateArchiveRequest;
import software.amazon.cloudformation.exceptions.CfnNotUpdatableException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.ARCHIVE_ID;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.ARCHIVE_KMS_ARN;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.ARCHIVE_NAME;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.fakeGetArchiveResponse;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.fakeListTagsForResourceResponse;
import static software.amazon.ses.mailmanagerarchive.HandlerHelper.fakeUpdateArchiveResponse;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    MailManagerClient mailManagerClient;
    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private ProxyClient<MailManagerClient> proxyClient;
    private ResourceModel desired_archive_model;
    private ResourceModel previous_archive_model;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        mailManagerClient = mock(MailManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, mailManagerClient);

        desired_archive_model = ResourceModel.builder()
                .archiveId(ARCHIVE_ID)
                .archiveName(ARCHIVE_NAME)
                .retention(ArchiveRetention.builder()
                        .retentionPeriod("FIVE_YEARS")
                        .build())
                .build();

        previous_archive_model = ResourceModel.builder()
                .archiveId(ARCHIVE_ID)
                .archiveName(ARCHIVE_NAME)
                .retention(ArchiveRetention.builder()
                        .retentionPeriod("THREE_MONTHS")
                        .build())
                .build();
    }

    @AfterEach
    public void tear_down(TestInfo testInfo) {
        if(testInfo.getTags().contains("SkipTearDown")) {
            return;
        }
        verify(mailManagerClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(mailManagerClient);
    }

    @Test
    public void handle_update_request_simple_success() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_archive_model)
                .previousResourceState(previous_archive_model)
                .build();

        when(mailManagerClient.getArchive(any(GetArchiveRequest.class))).thenReturn(fakeGetArchiveResponse());
        when(mailManagerClient.updateArchive(any(UpdateArchiveRequest.class))).thenReturn(fakeUpdateArchiveResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getArchiveName()).isEqualTo(ARCHIVE_NAME);
        assertThat(response.getResourceModel().getArchiveId()).isEqualTo(ARCHIVE_ID);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_update_request_failure_due_to_not_exist() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .archiveId(ARCHIVE_ID)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .archiveId(ARCHIVE_ID)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .build();

        when(mailManagerClient.getArchive(any(GetArchiveRequest.class))).thenThrow(ResourceNotFoundException.builder().message("Not Found").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("Not Found");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    @Tag("SkipTearDown")
    public void handle_update_request_failure_due_to_new_kms_key_provided() {
        final UpdateHandler handler = new UpdateHandler();

        desired_archive_model.setKmsKeyArn(ARCHIVE_KMS_ARN);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_archive_model)
                .previousResourceState(previous_archive_model)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handle_request_simple_success_with_tagging_only() {
        final UpdateHandler handler = new UpdateHandler();

        desired_archive_model.setTags(
                List.of(
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("KeyOne").value("valueOne").build(),
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("KeyTwo").value("valueTwo").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_archive_model)
                .previousResourceState(previous_archive_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateArchive(any(UpdateArchiveRequest.class))).thenReturn(fakeUpdateArchiveResponse());
        when(mailManagerClient.getArchive(any(GetArchiveRequest.class))).thenReturn(fakeGetArchiveResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getArchiveId()).isEqualTo(request.getDesiredResourceState().getArchiveId());
        assertThat(response.getResourceModel().getArchiveName()).isEqualTo(request.getDesiredResourceState().getArchiveName());
        assertThat(response.getResourceModel().getRetention()).isEqualTo(request.getDesiredResourceState().getRetention());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        ArgumentCaptor<TagResourceRequest> tagResourceCaptor = ArgumentCaptor.forClass(TagResourceRequest.class);

        verify(mailManagerClient).tagResource(tagResourceCaptor.capture());
        TagResourceRequest capturedRequest = tagResourceCaptor.getValue();
        assertThat(capturedRequest.tags().containsAll(List.of(
                software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                        .key("KeyOne")
                        .value("valueOne")
                        .build(),
                software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                        .key("KeyTwo")
                        .value("valueTwo")
                        .build()
        ))).isTrue();
        verify(mailManagerClient, never()).untagResource(any(UntagResourceRequest.class));
    }

    @Test
    public void handle_request_simple_success_with_tagging_and_untagging() {
        final UpdateHandler handler = new UpdateHandler();

        desired_archive_model.setTags(
                List.of(
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_1").value("value_1").build(),
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_2").value("new_value_2").build(),
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_3").value("value_3").build()
                )
        );

        previous_archive_model.setTags(
                List.of(
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_1").value("value_1").build(),
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_2").value("value_2").build(),
                        software.amazon.ses.mailmanagerarchive.Tag.builder().key("key_4").value("value_4").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_archive_model)
                .previousResourceState(previous_archive_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateArchive(any(UpdateArchiveRequest.class))).thenReturn(fakeUpdateArchiveResponse());
        when(mailManagerClient.getArchive(any(GetArchiveRequest.class))).thenReturn(fakeGetArchiveResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getArchiveName()).isEqualTo(request.getDesiredResourceState().getArchiveName());
        assertThat(response.getResourceModel().getArchiveId()).isEqualTo(request.getDesiredResourceState().getArchiveId());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        ArgumentCaptor<TagResourceRequest> tagResourceCaptor = ArgumentCaptor.forClass(TagResourceRequest.class);
        ArgumentCaptor<UntagResourceRequest> untagResourceCaptor = ArgumentCaptor.forClass(UntagResourceRequest.class);

        verify(mailManagerClient).tagResource(tagResourceCaptor.capture());
        verify(mailManagerClient).untagResource(untagResourceCaptor.capture());

        TagResourceRequest capturedTagRequest = tagResourceCaptor.getValue();
        UntagResourceRequest capturedUntagRequest = untagResourceCaptor.getValue();

        assertThat(capturedTagRequest.tags().containsAll(List.of(
                software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                        .key("key_2")
                        .value("new_value_2")
                        .build(),
                software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                        .key("key_3")
                        .value("value_3")
                        .build()
        ))).isTrue();

        assertThat(capturedUntagRequest.tagKeys().contains("key_4")).isTrue();
    }
}
