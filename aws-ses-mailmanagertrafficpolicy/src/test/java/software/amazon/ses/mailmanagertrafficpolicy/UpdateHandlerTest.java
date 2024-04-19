package software.amazon.ses.mailmanagertrafficpolicy;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.AcceptAction;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateTrafficPolicyRequest;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.TRAFFIC_POLICY_ID;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.TRAFFIC_POLICY_NAME;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeGetTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeListTagsForResourceResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeUpdateTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.generatePolicyStatements;
import static software.amazon.ses.mailmanagertrafficpolicy.utils.PolicyStatementConvertorFromSdk.ConvertFromSdk;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    MailManagerClient mailManagerClient;
    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private ProxyClient<MailManagerClient> proxyClient;
    private ResourceModel desired_traffic_policy_model;
    private ResourceModel previous_traffic_policy_model;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        mailManagerClient = mock(MailManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, mailManagerClient);

        desired_traffic_policy_model = ResourceModel.builder()
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .trafficPolicyId(TRAFFIC_POLICY_ID)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .maxMessageSizeBytes(100.0)
                .build();

        previous_traffic_policy_model = ResourceModel.builder()
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .trafficPolicyId(TRAFFIC_POLICY_ID)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .maxMessageSizeBytes(100.0)
                .build();
    }

    @AfterEach
    public void tear_down(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipTearDown")) {
            return;
        }
        verify(mailManagerClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(mailManagerClient);
    }

    @Test
    public void handle_request_simple_success() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_traffic_policy_model)
                .previousResourceState(previous_traffic_policy_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateTrafficPolicy(any(UpdateTrafficPolicyRequest.class))).thenReturn(fakeUpdateTrafficPolicyResponse());
        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getTrafficPolicyName()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyName());
        assertThat(response.getResourceModel().getDefaultAction()).isEqualTo(request.getDesiredResourceState().getDefaultAction());
        assertThat(response.getResourceModel().getMaxMessageSizeBytes()).isEqualTo(request.getDesiredResourceState().getMaxMessageSizeBytes());
        assertThat(response.getResourceModel().getPolicyStatements()).isEqualTo(request.getDesiredResourceState().getPolicyStatements());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_with_tagging_only() {
        final UpdateHandler handler = new UpdateHandler();

        desired_traffic_policy_model.setTags(
                List.of(
                        Tag.builder().key("KeyOne").value("valueOne").build(),
                        Tag.builder().key("KeyTwo").value("valueTwo").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_traffic_policy_model)
                .previousResourceState(previous_traffic_policy_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateTrafficPolicy(any(UpdateTrafficPolicyRequest.class))).thenReturn(fakeUpdateTrafficPolicyResponse());
        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getTrafficPolicyName()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyName());
        assertThat(response.getResourceModel().getDefaultAction()).isEqualTo(request.getDesiredResourceState().getDefaultAction());
        assertThat(response.getResourceModel().getMaxMessageSizeBytes()).isEqualTo(request.getDesiredResourceState().getMaxMessageSizeBytes());
        assertThat(response.getResourceModel().getPolicyStatements()).isEqualTo(request.getDesiredResourceState().getPolicyStatements());
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

        desired_traffic_policy_model.setTags(
                List.of(
                        Tag.builder().key("key_1").value("value_1").build(),
                        Tag.builder().key("key_2").value("new_value_2").build(),
                        Tag.builder().key("key_3").value("value_3").build()
                )
        );

        previous_traffic_policy_model.setTags(
                List.of(
                        Tag.builder().key("key_1").value("value_1").build(),
                        Tag.builder().key("key_2").value("value_2").build(),
                        Tag.builder().key("key_4").value("value_4").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_traffic_policy_model)
                .previousResourceState(previous_traffic_policy_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateTrafficPolicy(any(UpdateTrafficPolicyRequest.class))).thenReturn(fakeUpdateTrafficPolicyResponse());
        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getTrafficPolicyName()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyName());
        assertThat(response.getResourceModel().getDefaultAction()).isEqualTo(request.getDesiredResourceState().getDefaultAction());
        assertThat(response.getResourceModel().getMaxMessageSizeBytes()).isEqualTo(request.getDesiredResourceState().getMaxMessageSizeBytes());
        assertThat(response.getResourceModel().getPolicyStatements()).isEqualTo(request.getDesiredResourceState().getPolicyStatements());
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

    @Test
    public void handle_request_failure_due_to_resource_not_found() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_traffic_policy_model)
                .previousResourceState(desired_traffic_policy_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateTrafficPolicy(any(UpdateTrafficPolicyRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("resource not found");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
