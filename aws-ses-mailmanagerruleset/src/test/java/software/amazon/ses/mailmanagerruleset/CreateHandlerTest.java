package software.amazon.ses.mailmanagerruleset;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.CreateRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetRequest;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
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
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.CLIENT_REQUEST_TOKEN;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.LOGICAL_RESOURCE_ID;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.RULE_SET_NAME;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeCreateRuleSetResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeGetRuleSetResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.fakeListTagsForResourceResponse;
import static software.amazon.ses.mailmanagerruleset.HandlerHelper.generateRules;

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
    public void handle_request_simple_success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ruleSetName(RULE_SET_NAME)
                .rules(generateRules())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createRuleSet(any(CreateRuleSetRequest.class))).thenReturn(fakeCreateRuleSetResponse());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getRuleSetName()).isEqualTo(request.getDesiredResourceState().getRuleSetName());
        assertThat(response.getResourceModel().getRules().size()).isEqualTo(request.getDesiredResourceState().getRules().size());
        assertThat(response.getResourceModel().getRules().get(0).getName()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getName());
        assertThat(response.getResourceModel().getRules().get(0).getConditions()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getConditions());
        assertThat(response.getResourceModel().getRules().get(0).getActions()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getActions());
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_without_name() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .rules(generateRules())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createRuleSet(any(CreateRuleSetRequest.class))).thenReturn(fakeCreateRuleSetResponse());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getRuleSetName()).isNotNull();
        assertThat(response.getResourceModel().getRules().size()).isEqualTo(request.getDesiredResourceState().getRules().size());
        assertThat(response.getResourceModel().getRules().get(0).getName()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getName());
        assertThat(response.getResourceModel().getRules().get(0).getConditions()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getConditions());
        assertThat(response.getResourceModel().getRules().get(0).getActions()).isEqualTo(request.getDesiredResourceState().getRules().get(0).getActions());
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_with_tags() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ruleSetName(RULE_SET_NAME)
                .rules(generateRules())
                .tags(List.of(
                        Tag.builder().key("KeyOne").value("ValueOne").build(),
                        Tag.builder().key("KeyTwo").value("ValueTwo").build()
                ))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .systemTags(Map.of("SystemKeyOne", "SystemValueOne"))
                .desiredResourceTags(Map.of("StackKeyOne", "StackValueOne"))
                .build();

        when(mailManagerClient.createRuleSet(any(CreateRuleSetRequest.class))).thenReturn(fakeCreateRuleSetResponse());
        when(mailManagerClient.getRuleSet(any(GetRuleSetRequest.class))).thenReturn(fakeGetRuleSetResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        ArgumentCaptor<CreateRuleSetRequest> captor = ArgumentCaptor.forClass(CreateRuleSetRequest.class);

        verify(mailManagerClient).createRuleSet(captor.capture());
        assertThat(captor.getValue().ruleSetName()).isEqualTo(RULE_SET_NAME);
        assertThat(captor.getValue().tags().size()).isEqualTo(4);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRuleSetId()).isNotNull();
        assertThat(response.getResourceModel().getRuleSetArn()).isNotNull();
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_failure_due_to_conflict_exception() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .rules(generateRules())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createRuleSet(any(CreateRuleSetRequest.class))).thenThrow(ConflictException.builder().message("RuleSet already exists").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
        assertThat(response.getMessage()).isEqualTo("RuleSet already exists");
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
    }

    @Test
    public void handle_request_failure_due_to_unknown_exception() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .rules(generateRules())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createRuleSet(any(CreateRuleSetRequest.class))).thenThrow(new RuntimeException("unknown"));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.Unknown);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
    }
}
