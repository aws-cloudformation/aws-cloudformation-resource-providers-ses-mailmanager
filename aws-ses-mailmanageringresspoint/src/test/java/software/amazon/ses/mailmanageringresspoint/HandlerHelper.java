package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.services.mailmanager.model.CreateIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.GetIngressPointResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressPoint;
import software.amazon.awssdk.services.mailmanager.model.IngressPointStatus;
import software.amazon.awssdk.services.mailmanager.model.IngressPointType;
import software.amazon.awssdk.services.mailmanager.model.ListIngressPointsResponse;
import software.amazon.awssdk.services.mailmanager.model.UpdateIngressPointResponse;

public class HandlerHelper {
    public static final String INGRESS_POINT_ID = "ingressPoint_id";
    public static final String INGRESS_POINT_NAME = "ingressPoint_name";
    public static final String INGRESS_POINT_OPEN_RELAY = IngressPointType.OPEN_RELAY.toString();
    public static final String INGRESS_POINT_AUTH_RELAY = IngressPointType.AUTH_RELAY.toString();
    public static final String INGRESS_POINT_STATUS = IngressPointStatus.ACTIVE.toString();
    public static final String INGRESS_POINT_ARN = "ingressPoint_arn";
    public static final String INGRESS_POINT_A_RECORD = "ingressPoint_aRecord";
    public static final String INGRESS_POINT_RULE_SET_ID = "ingressPoint_ruleSet_id";
    public static final String INGRESS_POINT_TRAFFIC_POLICY_ID = "ingressPoint_trafficPolicy_id";
    public static final String INGRESS_POINT_AUTH_PASSWORD = "ingressPoint_auth_password";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";

    static GetIngressPointResponse fakeGetIngressPointResponse(String type, String status) {
        return GetIngressPointResponse.builder()
                .ingressPointName(INGRESS_POINT_NAME)
                .ingressPointId(INGRESS_POINT_ID)
                .ingressPointArn(INGRESS_POINT_ARN)
                .trafficPolicyId(INGRESS_POINT_TRAFFIC_POLICY_ID)
                .ruleSetId(INGRESS_POINT_RULE_SET_ID)
                .aRecord(INGRESS_POINT_A_RECORD)
                .type(type)
                .status(status)
                .build();
    }

    static CreateIngressPointResponse fakeCreateIngressPointResponse() {
        return CreateIngressPointResponse.builder()
                .ingressPointId(INGRESS_POINT_ID)
                .build();
    }

    static UpdateIngressPointResponse fakeUpdateIngressPointResponse() {
        return UpdateIngressPointResponse.builder().build();
    }

    static DeleteIngressPointResponse fakeDeleteIngressPointResponse() {
        return DeleteIngressPointResponse.builder().build();
    }

    static ListIngressPointsResponse fakeListIngressPointsResponse() {
        return ListIngressPointsResponse.builder().ingressPoints(
                IngressPoint.builder().ingressPointId("id_one").build(),
                IngressPoint.builder().ingressPointId("id_two").build()
        ).build();
    }
}
