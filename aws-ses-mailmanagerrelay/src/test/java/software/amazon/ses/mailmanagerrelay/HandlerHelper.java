package software.amazon.ses.mailmanagerrelay;

import software.amazon.awssdk.services.mailmanager.model.CreateRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.GetRelayResponse;
import software.amazon.awssdk.services.mailmanager.model.ListRelaysResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.RelayAuthentication;
import software.amazon.awssdk.services.mailmanager.model.Relay;
import software.amazon.awssdk.services.mailmanager.model.UpdateRelayResponse;


public class HandlerHelper {
    public static final String RELAY_ID = "relay_id";
    public static final String RELAY_NAME = "relay_name";
    public static final String RELAY_ARN = "relay_arn";
    public static final String RELAY_SERVICE_NAME = "relay_service_name";
    public static final Integer RELAY_SERVICE_PORT = 80;
    public static final String RELAY_SECRET_ARN = "relay_secret_arn";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";

    static GetRelayResponse fakeGetRelayResponse() {
        return GetRelayResponse.builder()
                .relayId(RELAY_ID)
                .relayName(RELAY_NAME)
                .relayArn(RELAY_ARN)
                .serverName(RELAY_SERVICE_NAME)
                .serverPort(RELAY_SERVICE_PORT)
                .authentication(RelayAuthentication.builder()
                        .secretArn(RELAY_SECRET_ARN)
                        .build()
                )
                .build();
    }

    static CreateRelayResponse fakeCreateRelayResponse() {
        return CreateRelayResponse.builder()
                .relayId(RELAY_ID)
                .build();
    }

    static UpdateRelayResponse fakeUpdateRelayResponse() {
        return UpdateRelayResponse.builder().build();
    }

    static DeleteRelayResponse fakeDeleteRelayResponse() {
        return DeleteRelayResponse.builder().build();
    }

    static ListRelaysResponse fakeListRelaysResponse() {
        return ListRelaysResponse.builder().relays(
                Relay.builder().relayId("id_one").build(),
                Relay.builder().relayId("id_two").build()
        ).build();
    }

    static ListTagsForResourceResponse fakeListTagsForResourceResponse() {
        return ListTagsForResourceResponse.builder()
                .tags(
                        software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                                .key("keyOne")
                                .value("valueOne")
                                .build(),
                        software.amazon.awssdk.services.mailmanager.model.Tag.builder()
                                .key("keyTwo")
                                .value("valueTwo")
                                .build()
                )
                .build();
    }
}
