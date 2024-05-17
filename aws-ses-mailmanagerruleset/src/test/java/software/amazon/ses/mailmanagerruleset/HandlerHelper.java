package software.amazon.ses.mailmanagerruleset;

import software.amazon.awssdk.services.mailmanager.model.CreateRuleSetResponse;
import software.amazon.awssdk.services.mailmanager.model.GetRuleSetResponse;
import software.amazon.awssdk.services.mailmanager.model.ListRuleSetsResponse;
import software.amazon.awssdk.services.mailmanager.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.mailmanager.model.RuleBooleanEmailAttribute;
import software.amazon.awssdk.services.mailmanager.model.RuleBooleanOperator;
import software.amazon.awssdk.services.mailmanager.model.RuleSet;
import software.amazon.awssdk.services.mailmanager.model.RuleVerdict;
import software.amazon.awssdk.services.mailmanager.model.RuleVerdictAttribute;
import software.amazon.awssdk.services.mailmanager.model.RuleVerdictOperator;
import software.amazon.awssdk.services.mailmanager.model.UpdateRuleSetResponse;
import software.amazon.ses.mailmanagerruleset.utils.RuleConvertorToSdk;

import java.util.Collections;
import java.util.List;

public class HandlerHelper {
    public static final String RULE_SET_ID = "rule_set_id";
    public static final String RULE_SET_ID_TWO = "rule_set_id_two";
    public static final String RULE_SET_NAME = "rule_set_name";
    public static final String RULE_NAME_ONE = "rule_name_one";
    public static final String RULE_NAME_TWO = "rule_name_two";
    public static final String RULE_SET_ARN = "rule_set_arn";
    public static final String CLIENT_REQUEST_TOKEN = "client_request_token";
    public static final String LOGICAL_RESOURCE_ID = "logical_resource_id";

    static CreateRuleSetResponse fakeCreateRuleSetResponse() {
        return CreateRuleSetResponse.builder()
                .ruleSetId(RULE_SET_ID)
                .build();
    }

    static UpdateRuleSetResponse fakeUpdateRuleSetResponse() {
        return UpdateRuleSetResponse.builder().build();
    }

    static GetRuleSetResponse fakeGetRuleSetResponse() {
        return GetRuleSetResponse.builder()
                .ruleSetId(RULE_SET_ID)
                .ruleSetName(RULE_SET_NAME)
                .ruleSetArn(RULE_SET_ARN)
                .rules(RuleConvertorToSdk.ConvertToSdk(generateRules()))
                .build();
    }

    static ListRuleSetsResponse fakeListRuleSetsResponse() {
        return ListRuleSetsResponse.builder()
                .ruleSets(
                        RuleSet.builder().ruleSetId(RULE_SET_ID).build(),
                        RuleSet.builder().ruleSetId(RULE_SET_ID_TWO).build()
                )
                .build();
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

    static List<Rule> generateRules() {
        return List.of(
                Rule.builder()
                        .name(RULE_NAME_ONE)
                        .conditions(List.of(
                                RuleCondition.builder()
                                        .booleanExpression(
                                                RuleBooleanExpression.builder()
                                                        .operator(RuleBooleanOperator.IS_FALSE.toString())
                                                        .evaluate(RuleBooleanToEvaluate.builder()
                                                                .attribute(RuleBooleanEmailAttribute.TLS.toString())
                                                                .build())
                                                        .build()
                                        )
                                        .build()
                        ))
                        .actions(List.of(
                                RuleAction.builder()
                                        .drop(Collections.emptyMap())
                                        .build(),
                                RuleAction.builder()
                                        .send(SendAction.builder()
                                                .roleArn("roleArn")
                                                .build())
                                        .build()
                        ))
                        .build(),
                Rule.builder()
                        .name(RULE_NAME_TWO)
                        .unless(List.of(
                                RuleCondition.builder()
                                        .verdictExpression(
                                                RuleVerdictExpression.builder()
                                                        .evaluate(
                                                                RuleVerdictToEvaluate.builder()
                                                                        .analysis(Analysis.builder()
                                                                                .analyzer("analyzer")
                                                                                .resultField("resultField")
                                                                                .build())
                                                                        .attribute(RuleVerdictAttribute.SPF.toString())
                                                                        .build()
                                                        )
                                                        .operator(RuleVerdictOperator.EQUALS.toString())
                                                        .values(List.of(
                                                                RuleVerdict.GRAY.toString(),
                                                                RuleVerdict.FAIL.toString()
                                                        ))
                                                        .build()
                                        )
                                        .build()
                        ))
                        .build()
        );
    }
}
