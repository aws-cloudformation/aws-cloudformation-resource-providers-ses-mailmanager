package software.amazon.ses.mailmanagerruleset.utils;

import software.amazon.ses.mailmanagerruleset.AddHeaderAction;
import software.amazon.ses.mailmanagerruleset.Analysis;
import software.amazon.ses.mailmanagerruleset.ArchiveAction;
import software.amazon.ses.mailmanagerruleset.DeliverToMailboxAction;
import software.amazon.ses.mailmanagerruleset.RelayAction;
import software.amazon.ses.mailmanagerruleset.ReplaceRecipientAction;
import software.amazon.ses.mailmanagerruleset.Rule;
import software.amazon.ses.mailmanagerruleset.RuleAction;
import software.amazon.ses.mailmanagerruleset.RuleBooleanExpression;
import software.amazon.ses.mailmanagerruleset.RuleBooleanToEvaluate;
import software.amazon.ses.mailmanagerruleset.RuleCondition;
import software.amazon.ses.mailmanagerruleset.RuleDmarcExpression;
import software.amazon.ses.mailmanagerruleset.RuleIpExpression;
import software.amazon.ses.mailmanagerruleset.RuleIpToEvaluate;
import software.amazon.ses.mailmanagerruleset.RuleNumberExpression;
import software.amazon.ses.mailmanagerruleset.RuleNumberToEvaluate;
import software.amazon.ses.mailmanagerruleset.RuleStringExpression;
import software.amazon.ses.mailmanagerruleset.RuleStringToEvaluate;
import software.amazon.ses.mailmanagerruleset.RuleVerdictExpression;
import software.amazon.ses.mailmanagerruleset.RuleVerdictToEvaluate;
import software.amazon.ses.mailmanagerruleset.S3Action;
import software.amazon.ses.mailmanagerruleset.SendAction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RuleConvertorFromSdk {
    public static List<Rule> ConvertFromSdk(List<software.amazon.awssdk.services.mailmanager.model.Rule> source) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(RuleConvertorFromSdk::ConvertFromSdk)
                .collect(Collectors.toList());
    }

    static Rule ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.Rule source) {
        if (source == null) {
            return null;
        }

        return Rule.builder()
                .name(source.name())
                .conditions(ConvertConditionsFromSdk(source.conditions()))
                .actions(ConvertRuleActionsFromSdk(source.actions()))
                .unless(ConvertConditionsFromSdk(source.unless()))
                .build();

    }

    static List<RuleCondition> ConvertConditionsFromSdk(List<software.amazon.awssdk.services.mailmanager.model.RuleCondition> source) {
        if (source == null) {
            return null;
        }

        return source.stream()
                .map(RuleConvertorFromSdk::ConvertFromSdk)
                .collect(Collectors.toList());

    }

    static List<RuleAction> ConvertRuleActionsFromSdk(List<software.amazon.awssdk.services.mailmanager.model.RuleAction> source) {
        if (source == null) {
            return null;
        }

        return source.stream()
                .map(RuleConvertorFromSdk::ConvertFromSdk)
                .collect(Collectors.toList());

    }

    static RuleAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleAction source) {
        if (source == null) {
            return null;
        }

        return RuleAction.builder()
                .addHeader(ConvertFromSdk(source.addHeader()))
                .relay(ConvertFromSdk(source.relay()))
                .send(ConvertFromSdk(source.send()))
                .drop(source.drop() != null ? Collections.emptyMap() : null)
                .archive(ConvertFromSdk(source.archive()))
                .deliverToMailbox(ConvertFromSdk(source.deliverToMailbox()))
                .replaceRecipient(ConvertFromSdk(source.replaceRecipient()))
                .writeToS3(ConvertFromSdk(source.writeToS3()))
                .build();
    }

    static AddHeaderAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.AddHeaderAction source) {
        if (source == null) {
            return null;
        }

        return AddHeaderAction.builder()
                .headerName(source.headerName())
                .headerValue(source.headerValue())
                .build();
    }

    static RelayAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RelayAction source) {
        if (source == null) {
            return null;
        }

        return RelayAction.builder()
                .mailFrom(source.mailFromAsString())
                .relay(source.relay())
                .build();
    }

    static SendAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.SendAction source) {
        if (source == null) {
            return null;
        }

        return SendAction.builder()
                .roleArn(source.roleArn())
                .build();
    }

    static ArchiveAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.ArchiveAction source) {
        if (source == null) {
            return null;
        }

        return ArchiveAction.builder()
                .targetArchive(source.targetArchive())
                .build();
    }

    static DeliverToMailboxAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.DeliverToMailboxAction source) {
        if (source == null) {
            return null;
        }

        return DeliverToMailboxAction.builder()
                .mailboxArn(source.mailboxArn())
                .roleArn(source.roleArn())
                .build();
    }

    static ReplaceRecipientAction ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.ReplaceRecipientAction source) {
        if (source == null) {
            return null;
        }

        return ReplaceRecipientAction.builder()
                .replaceWith(source.replaceWith())
                .build();
    }

    static S3Action ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.S3Action source) {
        if (source == null) {
            return null;
        }

        return S3Action.builder()
                .roleArn(source.roleArn())
                .s3Bucket(source.s3Bucket())
                .s3Prefix(source.s3Prefix())
                .s3SseKmsKeyId(source.s3SseKmsKeyId())
                .build();
    }

    static RuleCondition ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleCondition source) {
        if (source == null) {
            return null;
        }

        return RuleCondition.builder()
                .booleanExpression(ConvertFromSdk(source.booleanExpression()))
                .dmarcExpression(ConvertFromSdk(source.dmarcExpression()))
                .numberExpression(ConvertFromSdk(source.numberExpression()))
                .stringExpression(ConvertFromSdk(source.stringExpression()))
                .ipExpression(ConvertFromSdk(source.ipExpression()))
                .verdictExpression(ConvertFromSdk(source.verdictExpression()))
                .build();
    }

    // Expression
    static RuleBooleanExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleBooleanExpression source) {
        if (source == null) {
            return null;
        }

        return RuleBooleanExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .build();
    }

    static RuleDmarcExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleDmarcExpression source) {
        if (source == null) {
            return null;
        }

        return RuleDmarcExpression.builder()
                .operator(source.operatorAsString())
                .values(source.valuesAsStrings())
                .build();
    }

    static RuleNumberExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleNumberExpression source) {
        if (source == null) {
            return null;
        }

        return RuleNumberExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .value(source.value())
                .build();
    }

    static RuleStringExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleStringExpression source) {
        if (source == null) {
            return null;
        }

        return RuleStringExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .values(source.values())
                .build();
    }

    static RuleIpExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleIpExpression source) {
        if (source == null) {
            return null;
        }

        return RuleIpExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .values(source.values())
                .build();
    }

    static RuleVerdictExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleVerdictExpression source) {
        if (source == null) {
            return null;
        }

        return RuleVerdictExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .values(source.valuesAsStrings())
                .build();
    }

    // RuleToEvaluate
    static RuleBooleanToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleBooleanToEvaluate source) {
        if (source == null) {
            return null;
        }

        return RuleBooleanToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }

    static RuleNumberToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleNumberToEvaluate source) {
        if (source == null) {
            return null;
        }

        return RuleNumberToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }

    static RuleStringToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleStringToEvaluate source) {
        if (source == null) {
            return null;
        }

        return RuleStringToEvaluate.builder()
                .attribute(source.attributeAsString())
                .mimeHeaderAttribute(source.mimeHeaderAttribute())
                .build();
    }

    static RuleIpToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleIpToEvaluate source) {
        if (source == null) {
            return null;
        }

        return RuleIpToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }

    static RuleVerdictToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.RuleVerdictToEvaluate source) {
        if (source == null) {
            return null;
        }

        return RuleVerdictToEvaluate.builder()
                .attribute(source.attributeAsString())
                .analysis(ConvertFromSdk(source.analysis()))
                .build();
    }

    // Analysis
    static Analysis ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.Analysis source) {
        if (source == null) {
            return null;
        }

        return Analysis.builder()
                .analyzer(source.analyzer())
                .resultField(source.resultField())
                .build();
    }
}
