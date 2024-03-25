package software.amazon.ses.mailmanagertrafficpolicy;

import java.time.Duration;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.AcceptAction;
import software.amazon.awssdk.services.mailmanager.model.DeleteTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyRequest;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
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
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeDeleteTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.fakeGetTrafficPolicyResponse;
import static software.amazon.ses.mailmanagertrafficpolicy.HandlerHelper.generatePolicyStatements;
import static software.amazon.ses.mailmanagertrafficpolicy.utils.PolicyStatementConvertorFromSdk.ConvertFromSdk;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

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
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder()
                .trafficPolicyName(HandlerHelper.TRAFFIC_POLICY_NAME)
                .trafficPolicyId(HandlerHelper.TRAFFIC_POLICY_ID)
                .trafficPolicyArn(HandlerHelper.TRAFFIC_POLICY_ARN)
                .maxMessageSizeBytes(100.0)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenReturn(fakeGetTrafficPolicyResponse());
        when(mailManagerClient.deleteTrafficPolicy(any(DeleteTrafficPolicyRequest.class))).thenReturn(fakeDeleteTrafficPolicyResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_failure_due_to_resource_not_found() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder()
                .trafficPolicyName(HandlerHelper.TRAFFIC_POLICY_NAME)
                .trafficPolicyId(HandlerHelper.TRAFFIC_POLICY_ID)
                .trafficPolicyArn(HandlerHelper.TRAFFIC_POLICY_ARN)
                .maxMessageSizeBytes(100.0)
                .policyStatements(ConvertFromSdk(generatePolicyStatements()))
                .defaultAction(AcceptAction.ALLOW.toString())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.getTrafficPolicy(any(GetTrafficPolicyRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNotNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("resource not found");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
