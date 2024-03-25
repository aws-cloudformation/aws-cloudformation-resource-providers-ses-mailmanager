package software.amazon.ses.mailmanagertrafficpolicy;

import software.amazon.awssdk.services.mailmanager.model.AcceptAction;
import software.amazon.awssdk.services.mailmanager.model.CreateTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.DeleteTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.GetTrafficPolicyResponse;
import software.amazon.awssdk.services.mailmanager.model.IngressAnalysis;
import software.amazon.awssdk.services.mailmanager.model.IngressBooleanExpression;
import software.amazon.awssdk.services.mailmanager.model.IngressBooleanOperator;
import software.amazon.awssdk.services.mailmanager.model.IngressBooleanToEvaluate;
import software.amazon.awssdk.services.mailmanager.model.IngressStringEmailAttribute;
import software.amazon.awssdk.services.mailmanager.model.IngressStringExpression;
import software.amazon.awssdk.services.mailmanager.model.IngressStringOperator;
import software.amazon.awssdk.services.mailmanager.model.IngressStringToEvaluate;
import software.amazon.awssdk.services.mailmanager.model.ListTrafficPoliciesResponse;
import software.amazon.awssdk.services.mailmanager.model.PolicyCondition;
import software.amazon.awssdk.services.mailmanager.model.PolicyStatement;
import software.amazon.awssdk.services.mailmanager.model.TrafficPolicy;
import software.amazon.awssdk.services.mailmanager.model.UpdateTrafficPolicyResponse;

import java.util.List;

public class HandlerHelper {
    public static final String TRAFFIC_POLICY_ID = "traffic_policy_id";
    public static final String TRAFFIC_POLICY_NAME = "traffic_policy_name";
    public static final String TRAFFIC_POLICY_ARN = "traffic_policy_arn";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";
    static GetTrafficPolicyResponse fakeGetTrafficPolicyResponse() {

        return GetTrafficPolicyResponse.builder()
                .trafficPolicyId(TRAFFIC_POLICY_ID)
                .trafficPolicyArn(TRAFFIC_POLICY_ARN)
                .trafficPolicyName(TRAFFIC_POLICY_NAME)
                .policyStatements(generatePolicyStatements())
                .defaultAction(AcceptAction.ALLOW)
                .maxMessageSizeBytes(100)
                .build();
    }

    static PolicyCondition generateStringPolicyCondition() {
        return PolicyCondition.builder()
                .stringExpression(
                        IngressStringExpression.builder()
                                .evaluate(IngressStringToEvaluate.fromAttribute(IngressStringEmailAttribute.RECIPIENT))
                                .value("zlinzhou@amazon.com")
                                .operator(IngressStringOperator.EQUALS)
                                .build()
                )
                .build();
    }

    static PolicyCondition generateBooleanPolicyCondition() {
        return PolicyCondition.builder()
                .booleanExpression(
                        IngressBooleanExpression.builder()
                                .evaluate(IngressBooleanToEvaluate.builder()
                                        .analysis(IngressAnalysis.builder()
                                                .analyzer("Analyze")
                                                .resultField("ResultField")
                                                .build())
                                        .build())
                                .operator(IngressBooleanOperator.IS_TRUE)
                                .build()
                )
                .build();
    }

    static List<PolicyStatement> generatePolicyStatements() {
        return List.of(
                PolicyStatement.builder()
                        .conditions(generateStringPolicyCondition())
                        .action(AcceptAction.ALLOW)
                        .build(),
                PolicyStatement.builder()
                        .conditions(generateBooleanPolicyCondition())
                        .action(AcceptAction.ALLOW)
                        .build()
        );
    }

    static CreateTrafficPolicyResponse fakeCreateTrafficPolicyResponse() {
        return CreateTrafficPolicyResponse.builder()
                .trafficPolicyId(TRAFFIC_POLICY_ID)
                .build();
    }

    static UpdateTrafficPolicyResponse fakeUpdateTrafficPolicyResponse() {
        return UpdateTrafficPolicyResponse.builder().build();
    }

    static DeleteTrafficPolicyResponse fakeDeleteTrafficPolicyResponse() {
        return DeleteTrafficPolicyResponse.builder().build();
    }

    static ListTrafficPoliciesResponse fakeListTrafficPoliciesResponse() {
        return ListTrafficPoliciesResponse.builder().trafficPolicies(
                TrafficPolicy.builder().trafficPolicyId("id_one").build(),
                TrafficPolicy.builder().trafficPolicyId("id_two").build()
        ).build();
    }
}
