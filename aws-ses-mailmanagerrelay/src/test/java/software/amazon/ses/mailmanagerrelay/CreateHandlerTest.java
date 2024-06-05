package software.amazon.ses.mailmanagerrelay;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.CreateRelayRequest;
import software.amazon.awssdk.services.mailmanager.model.GetRelayRequest;
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
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.CLIENT_REQUEST_TOKEN;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.LOGICAL_RESOURCE_ID;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.RELAY_NAME;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.RELAY_SECRET_ARN;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.RELAY_SERVICE_NAME;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.RELAY_SERVICE_PORT;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.fakeCreateRelayResponse;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.fakeGetRelayResponse;
import static software.amazon.ses.mailmanagerrelay.HandlerHelper.fakeListTagsForResourceResponse;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<MailManagerClient> proxyClient;

    @Mock
    MailManagerClient mailManagerClient;

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
                .relayName(RELAY_NAME)
                .serverName(RELAY_SERVICE_NAME)
                .serverPort(RELAY_SERVICE_PORT.doubleValue())
                .authentication(RelayAuthentication.builder()
                        .secretArn(RELAY_SECRET_ARN)
                        .build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.createRelay(any(CreateRelayRequest.class))).thenReturn(fakeCreateRelayResponse());
        when(mailManagerClient.getRelay(any(GetRelayRequest.class))).thenReturn(fakeGetRelayResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRelayId()).isNotNull();
        assertThat(response.getResourceModel().getRelayArn()).isNotNull();
        assertThat(response.getResourceModel().getRelayName()).isEqualTo(request.getDesiredResourceState().getRelayName());
        assertThat(response.getResourceModel().getServerName()).isEqualTo(request.getDesiredResourceState().getServerName());
        assertThat(response.getResourceModel().getServerPort()).isEqualTo(request.getDesiredResourceState().getServerPort());
        assertThat(response.getResourceModel().getAuthentication()).isNotNull();
        assertThat(response.getResourceModel().getAuthentication().getSecretArn()).isEqualTo(RELAY_SECRET_ARN);
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_simple_success_without_name() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .serverName(RELAY_SERVICE_NAME)
                .serverPort(RELAY_SERVICE_PORT.doubleValue())
                .authentication(RelayAuthentication.builder()
                        .secretArn(RELAY_SECRET_ARN)
                        .build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .logicalResourceIdentifier(LOGICAL_RESOURCE_ID)
                .clientRequestToken(CLIENT_REQUEST_TOKEN)
                .build();

        when(mailManagerClient.createRelay(any(CreateRelayRequest.class))).thenReturn(fakeCreateRelayResponse());
        when(mailManagerClient.getRelay(any(GetRelayRequest.class))).thenReturn(fakeGetRelayResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRelayId()).isNotNull();
        assertThat(response.getResourceModel().getRelayArn()).isNotNull();
        assertThat(response.getResourceModel().getRelayName()).isNotNull();
        assertThat(response.getResourceModel().getServerName()).isEqualTo(request.getDesiredResourceState().getServerName());
        assertThat(response.getResourceModel().getServerPort()).isEqualTo(request.getDesiredResourceState().getServerPort());
        assertThat(response.getResourceModel().getAuthentication()).isNotNull();
        assertThat(response.getResourceModel().getAuthentication().getSecretArn()).isEqualTo(RELAY_SECRET_ARN);
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
                .relayName(RELAY_NAME)
                .serverName(RELAY_SERVICE_NAME)
                .serverPort(RELAY_SERVICE_PORT.doubleValue())
                .authentication(RelayAuthentication.builder()
                        .secretArn(RELAY_SECRET_ARN)
                        .build())
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

        when(mailManagerClient.createRelay(any(CreateRelayRequest.class))).thenReturn(fakeCreateRelayResponse());
        when(mailManagerClient.getRelay(any(GetRelayRequest.class))).thenReturn(fakeGetRelayResponse());
        when(mailManagerClient.listTagsForResource(any(ListTagsForResourceRequest.class))).thenReturn(fakeListTagsForResourceResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        ArgumentCaptor<CreateRelayRequest> captor = ArgumentCaptor.forClass(CreateRelayRequest.class);

        verify(mailManagerClient).createRelay(captor.capture());
        assertThat(captor.getValue().relayName()).isEqualTo(RELAY_NAME);
        assertThat(captor.getValue().tags().size()).isEqualTo(4);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getRelayId()).isNotNull();
        assertThat(response.getResourceModel().getRelayArn()).isNotNull();
        assertThat(response.getResourceModel().getTags()).isNotNull();
        assertThat(response.getResourceModel().getTags().size()).isEqualTo(2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handle_request_failure() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .relayName(RELAY_NAME)
                .serverName(RELAY_SERVICE_NAME)
                .serverPort(RELAY_SERVICE_PORT.doubleValue())
                .authentication(RelayAuthentication.builder()
                        .secretArn(RELAY_SECRET_ARN)
                        .build())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        when(mailManagerClient.createRelay(any(CreateRelayRequest.class))).thenThrow(ConflictException.builder().message("Conflict exception is thrown here").build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("Conflict exception is thrown here");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }
}
