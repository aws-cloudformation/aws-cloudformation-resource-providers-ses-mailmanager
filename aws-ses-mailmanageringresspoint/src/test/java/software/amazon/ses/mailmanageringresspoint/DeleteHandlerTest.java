package software.amazon.ses.mailmanageringresspoint;

import java.time.Duration;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
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
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .ingressPointId(HandlerHelper.INGRESS_POINT_ID)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .ingressPointStatus(HandlerHelper.INGRESS_POINT_STATUS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.deleteIngressPoint(any(DeleteIngressPointRequest.class))).thenReturn(HandlerHelper.fakeDeleteIngressPointResponse());
        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class)))
                .thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_OPEN_RELAY, IngressPointStatus.DEPROVISIONING.toString())) // Reproduce stabilization process
                .thenThrow(ResourceNotFoundException.builder().message("IngressPoint does not exist").build());

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
    public void handleRequest_failure_due_to_resource_not_found() {
        final DeleteHandler handler = new DeleteHandler();

        final ResourceModel model = ResourceModel.builder()
                .ingressPointName(HandlerHelper.INGRESS_POINT_NAME)
                .ingressPointId(HandlerHelper.INGRESS_POINT_ID)
                .trafficPolicyId(HandlerHelper.INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(HandlerHelper.INGRESS_POINT_RULE_SET_ID)
                .type(HandlerHelper.INGRESS_POINT_OPEN_RELAY)
                .ingressPointStatus(HandlerHelper.INGRESS_POINT_STATUS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .clientRequestToken(HandlerHelper.CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.deleteIngressPoint(any(DeleteIngressPointRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

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
