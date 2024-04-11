package software.amazon.ses.mailmanageringresspoint;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.awssdk.services.mailmanager.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static software.amazon.ses.mailmanageringresspoint.HandlerHelper.LOGICAL_RESOURCE_ID;

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
    public void tear_down(TestInfo testInfo) {
        if(testInfo.getTags().contains("SkipTearDown")) {
            return;
        }
        verify(mailManagerClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(mailManagerClient);
    }

    @Test
    public void handle_request_simple_success() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_AUTH_RELAY)
                .ingressPointConfiguration(
                        software.amazon.ses.mailmanageringresspoint.IngressPointConfiguration.builder()
                                .smtpPassword(HandlerHelper.INGRESS_POINT_AUTH_PASSWORD)
                                .build()
                )
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createIngressPoint(any(CreateIngressPointRequest.class))).thenReturn(HandlerHelper.fakeCreateIngressPointResponse());
        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class)))
                .thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_AUTH_RELAY, IngressPointStatus.PROVISIONING.toString())) // Reproduce stabilization process
                .thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_AUTH_RELAY, IngressPointStatus.ACTIVE.toString()));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getIngressPointName()).isEqualTo(request.getDesiredResourceState().getIngressPointName());
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getType()).isEqualTo(request.getDesiredResourceState().getType());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_without_providing_resource_name() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createIngressPoint(any(CreateIngressPointRequest.class))).thenReturn(HandlerHelper.fakeCreateIngressPointResponse());
        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class))).thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_OPEN_RELAY, IngressPointStatus.ACTIVE.toString()));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(request.getDesiredResourceState().getIngressPointName()).contains(LOGICAL_RESOURCE_ID);
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getType()).isEqualTo(request.getDesiredResourceState().getType());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    @Tag("SkipTearDown")
    public void handle_request_failure_due_to_providing_open_relay_with_unneeded_config() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .ingressPointConfiguration(
                        software.amazon.ses.mailmanageringresspoint.IngressPointConfiguration.builder()
                                .smtpPassword(HandlerHelper.INGRESS_POINT_AUTH_PASSWORD)
                                .build()
                )
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handle_request_failure_due_to_conflict() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createIngressPoint(any(CreateIngressPointRequest.class))).thenThrow(ConflictException.builder().message("IngressPoint already exists").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo("IngressPoint already exists");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void handle_request_failure_due_to_validation() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createIngressPoint(any(CreateIngressPointRequest.class))).thenThrow(ValidationException.builder().message("IngressPoint validation failure").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo("IngressPoint validation failure");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
}
