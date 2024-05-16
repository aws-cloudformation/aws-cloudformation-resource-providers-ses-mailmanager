package software.amazon.ses.mailmanageringresspoint;

import java.time.Duration;

import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointRequest;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceRequest;
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
import static software.amazon.ses.mailmanageringresspoint.HandlerHelper.fakeListTagsForResourceResponse;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

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
        final ReadHandler handler = new ReadHandler();

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

        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class))).thenReturn(HandlerHelper.fakeGetIngressPointResponse(HandlerHelper.INGRESS_POINT_OPEN_RELAY, IngressPointStatus.ACTIVE.toString()));
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getIngressPointName()).isEqualTo(request.getDesiredResourceState().getIngressPointName());
        assertThat(response.getResourceModel().getTrafficPolicyId()).isEqualTo(request.getDesiredResourceState().getTrafficPolicyId());
        assertThat(response.getResourceModel().getRuleSetId()).isEqualTo(request.getDesiredResourceState().getRuleSetId());
        assertThat(response.getResourceModel().getType()).isEqualTo(request.getDesiredResourceState().getType());
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_failure_due_to_resource_not_found() {
        final ReadHandler handler = new ReadHandler();

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

        when(mailManagerClient.getIngressPoint(any(GetIngressPointRequest.class))).thenThrow(ResourceNotFoundException.builder().message("resource not found").build());

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
