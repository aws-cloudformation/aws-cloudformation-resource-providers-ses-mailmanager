package software.amazon.ses.mailmanagertrafficpolicy;

import java.time.Duration;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.AcceptAction;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.CreateTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.ValidationException;
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
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.CLIENT_REQUEST_TOKEN;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.LOGICAL_RESOURCE_ID;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.TRAFFIC_POLICY_ARN;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.TRAFFIC_POLICY_ID;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.TRAFFIC_POLICY_NAME;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeCreateTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeGetTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.generatePolicyStatements;
import static software.amazon.ses.mailmanagertrafficpolicy.utils.PolicyStatementConvertorFromSdk.ConvertFromSdk;

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
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createTrafficPolicy(any(CreateTrafficPolicyRequest.class))).thenReturn(fakeCreateTrafficPolicyResponse());
        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNotNull();
        assertThat(response.getResourceModel().getTrafficPolicyName()).isEqualTo(TRAFFIC_POLICY_NAME);
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(TRAFFIC_POLICY_ID);
        assertThat(response.getResourceModel().getTrafficPolicyArn()).isEqualTo(TRAFFIC_POLICY_ARN);
        assertThat(response.getResourceModel().getMaxMessageSizeBytes()).isNotNull();
        assertThat(response.getResourceModel().getPolicyStatements().size()).isEqualTo(2);
        assertThat(response.getResourceModel().getPolicyStatements().get(0).getConditions().size()).isEqualTo(1);
        assertThat(response.getResourceModel().getPolicyStatements().get(1).getConditions().size()).isEqualTo(1);
        assertThat(response.getResourceModel().getDefaultAction()).isEqualTo(AcceptAction.ALLOW.toString());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_without_name() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .build();

        when(mailManagerClient.createTrafficPolicy(any(CreateTrafficPolicyRequest.class))).thenReturn(fakeCreateTrafficPolicyResponse());
        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNotNull();
        assertThat(response.getResourceModel().getTrafficPolicyName()).isNotNull();
        assertThat(model.getTrafficPolicyName()).isNotNull();
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(TRAFFIC_POLICY_ID);
        assertThat(response.getResourceModel().getTrafficPolicyArn()).isEqualTo(TRAFFIC_POLICY_ARN);
        assertThat(response.getResourceModel().getMaxMessageSizeBytes()).isNotNull();
        assertThat(response.getResourceModel().getPolicyStatements().size()).isEqualTo(2);
        assertThat(response.getResourceModel().getPolicyStatements().get(0).getConditions().size()).isEqualTo(1);
        assertThat(response.getResourceModel().getPolicyStatements().get(1).getConditions().size()).isEqualTo(1);
        assertThat(response.getResourceModel().getDefaultAction()).isEqualTo(AcceptAction.ALLOW.toString());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_failed_due_to_conflict() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createTrafficPolicy(any(CreateTrafficPolicyRequest.class))).thenThrow(ConflictException.builder().message("TrafficPolicy already exists").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo("TrafficPolicy already exists");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void handle_request_failed_due_to_validation() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createTrafficPolicy(any(CreateTrafficPolicyRequest.class))).thenThrow(ValidationException.builder().message("TrafficPolicy validation failure").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo("TrafficPolicy validation failure");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

}
