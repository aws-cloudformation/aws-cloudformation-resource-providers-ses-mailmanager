package software.amazon.ses.mailmanageringresspoint;

import java.time.Duration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointRequest;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    MailManagerClient mailManagerClient;
    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private ProxyClient<MailManagerClient> proxyClient;

    private ResourceModel open_relay_model;
    private ResourceModel auth_relay_model;
    private ResourceModel open_relay_with_config;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        mailManagerClient = mock(MailManagerClient.class);
        proxyClient = MOCK_PROXY(proxy, mailManagerClient);
        open_relay_model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .ingressPointId(HandlerHelper.INGRESS_POINT_ID)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .ingressPointStatus(HandlerHelper.INGRESS_POINT_STATUS)
                .build();
        auth_relay_model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .ingressPointId(HandlerHelper.INGRESS_POINT_ID)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_AUTH_RELAY)
                .ingressPointStatus(HandlerHelper.INGRESS_POINT_STATUS)
                .ingressPointConfiguration(
                        software.amazon.ses.mailmanageringresspoint.IngressPointConfiguration.builder()
                                .smtpPassword(HandlerHelper.INGRESS_POINT_AUTH_PASSWORD)
                                .build()
                )
                .build();
        open_relay_with_config = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .ingressPointId(HandlerHelper.INGRESS_POINT_ID)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .ingressPointStatus(HandlerHelper.INGRESS_POINT_STATUS)
                .ingressPointConfiguration(
                        software.amazon.ses.mailmanageringresspoint.IngressPointConfiguration.builder()
                                .smtpPassword(HandlerHelper.INGRESS_POINT_AUTH_PASSWORD)
                                .build()
                )
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
                .desiredResourceState(open_relay_model)
                .previousResourceState(open_relay_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateIngressPoint(any(UpdateIngressPointRequest.class))).thenReturn(HandlerHelper.fakeUpdateIngressPointResponse());
        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class)))
                .thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_OPEN_RELAY, IngressPointStatus.UPDATING.toString())) // Reproduce stabilization process
                .thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_OPEN_RELAY, IngressPointStatus.ACTIVE.toString()));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getIngressPointName()).isEqualTo(request.getDesiredResourceState().getIngressPointName());
        assertThat(response.getResourceModel().getIngressPointId()).isEqualTo(request.getDesiredResourceState().getIngressPointId());
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getType()).isEqualTo(request.getDesiredResourceState().getType());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    @Tag("SkipTearDown")
    public void handle_request_failure_due_to_updating_open_relay_with_unneeded_config() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(open_relay_with_config)
                .previousResourceState(open_relay_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    @Tag("SkipTearDown")
    public void handle_request_failure_due_to_updating_immutable_field() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(open_relay_model)
                .previousResourceState(auth_relay_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        assertThrows(CfnNotUpdatableException.class, () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger));
    }

    @Test
    public void handle_request_failure_due_to_resource_not_found() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(open_relay_model)
                .previousResourceState(open_relay_model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.updateIngressPoint(any(UpdateIngressPointRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

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
