package software.amazon.ses.mailmanagerruleset;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.mailmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetResponse;
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
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.RULE_SET_ID;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.RULE_SET_NAME;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeGetRuleSetResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeListTagsForResourceResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeUpdateRuleSetResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.generateRules;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<MailManagerClient> proxyClient;

    @Mock
    MailManagerClient mailManagerClient;
    private ResourceModel desired_rule_set_model;
    private ResourceModel previous_rule_set_model;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        mailManagerClient = mock(MailManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, mailManagerClient);

        desired_rule_set_model = ResourceModel.builder()
                .ruleSetName(RULE_SET_NAME)
                .ruleSetId(RULE_SET_ID)
                .rules(generateRules())
                .build();

        previous_rule_set_model = ResourceModel.builder()
                .ruleSetName(RULE_SET_NAME)
                .ruleSetId(RULE_SET_ID)
                .rules(generateRules())
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

        final ResourceModel model = ResourceModel.builder()
                .rules(generateRules())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.updateRuleSet(any(UpdateRuleSetRequest.class))).thenReturn(fakeUpdateRuleSetResponse());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRules()).isNotNull();
        assertThat(response.getResourceModel().getRules()).isNotEmpty();
        assertThat(response.getResourceModel().getRules().size()).isEqualTo(request.getDesiredResourceState().getRules().size());
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_with_tagging_only() {
        final UpdateHandler handler = new UpdateHandler();

        desired_rule_set_model.setTags(
                List.of(
                        Tag.builder().key("KeyOne").value("valueOne").build(),
                        Tag.builder().key("KeyTwo").value("valueTwo").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_rule_set_model)
                .previousResourceState(previous_rule_set_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateRuleSet(any(UpdateRuleSetRequest.class))).thenReturn(UpdateRuleSetResponse.builder().build());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRules()).isNotNull();
        assertThat(response.getResourceModel().getRules()).isNotEmpty();
        assertThat(response.getResourceModel().getRules().size()).isEqualTo(request.getDesiredResourceState().getRules().size());
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
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

        desired_rule_set_model.setTags(
                List.of(
                        Tag.builder().key("key_1").value("value_1").build(),
                        Tag.builder().key("key_2").value("new_value_2").build(),
                        Tag.builder().key("key_3").value("value_3").build()
                )
        );

        previous_rule_set_model.setTags(
                List.of(
                        Tag.builder().key("key_1").value("value_1").build(),
                        Tag.builder().key("key_2").value("value_2").build(),
                        Tag.builder().key("key_4").value("value_4").build()
                )
        );

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(desired_rule_set_model)
                .previousResourceState(previous_rule_set_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateRuleSet(any(UpdateRuleSetRequest.class))).thenReturn(UpdateRuleSetResponse.builder().build());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());
        when(mailManagerClient.untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRules()).isNotNull();
        assertThat(response.getResourceModel().getRules()).isNotEmpty();
        assertThat(response.getResourceModel().getRules().size()).isEqualTo(request.getDesiredResourceState().getRules().size());
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

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.updateRuleSet(any(UpdateRuleSetRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

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
