package software.amazon.ses.mailmanagerruleset.utils;

import software.amazon.awssdk.services.mailmanager.model.DropAction;
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

import java.util.List;
import java.util.stream.Collectors;

public class RuleConvertorToSdk {

    public static List<software.amazon.awssdk.services.mailmanager.model.Rule> ConvertToSdk(List<Rule> source) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(RuleConvertorToSdk::ConvertToSdk)
                .collect(Collectors.toList());
    }

    static software.amazon.awssdk.services.mailmanager.model.Rule ConvertToSdk(Rule source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.Rule.builder()
                .name(source.getName())
                .conditions(ConvertConditionsToSdk(source.getConditions()))
                .actions(ConvertRuleActionsToSdk(source.getActions()))
                .unless(ConvertConditionsToSdk(source.getUnless()))
                .build();
    }

    static List<software.amazon.awssdk.services.mailmanager.model.RuleCondition> ConvertConditionsToSdk(List<RuleCondition> source) {
        if (source == null) {
            return null;
        }

        return source.stream()
                .map(RuleConvertorToSdk::ConvertToSdk)
                .collect(Collectors.toList());

    }

    static List<software.amazon.awssdk.services.mailmanager.model.RuleAction> ConvertRuleActionsToSdk(List<RuleAction> source) {
        if (source == null) {
            return null;
        }

        return source.stream()
                .map(RuleConvertorToSdk::ConvertToSdk)
                .collect(Collectors.toList());

    }

    static software.amazon.awssdk.services.mailmanager.model.RuleAction ConvertToSdk(RuleAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleAction.builder()
                .addHeader(ConvertToSdk(source.getAddHeader()))
                .relay(ConvertToSdk(source.getRelay()))
                .send(ConvertToSdk(source.getSend()))
                .drop(source.getDrop() != null ? DropAction.builder().build() : null)
                .archive(ConvertToSdk(source.getArchive()))
                .deliverToMailbox(ConvertToSdk(source.getDeliverToMailbox()))
                .replaceRecipient(ConvertToSdk(source.getReplaceRecipient()))
                .writeToS3(ConvertToSdk(source.getWriteToS3()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.AddHeaderAction ConvertToSdk(AddHeaderAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.AddHeaderAction.builder()
                .headerName(source.getHeaderName())
                .headerValue(source.getHeaderValue())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RelayAction ConvertToSdk(RelayAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RelayAction.builder()
                .mailFrom(source.getMailFrom())
                .relay(source.getRelay())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.SendAction ConvertToSdk(SendAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.SendAction.builder()
                .roleArn(source.getRoleArn())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.ArchiveAction ConvertToSdk(ArchiveAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.ArchiveAction.builder()
                .targetArchive(source.getTargetArchive())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.DeliverToMailboxAction ConvertToSdk(DeliverToMailboxAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.DeliverToMailboxAction.builder()
                .mailboxArn(source.getMailboxArn())
                .roleArn(source.getRoleArn())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.ReplaceRecipientAction ConvertToSdk(ReplaceRecipientAction source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.ReplaceRecipientAction.builder()
                .replaceWith(source.getReplaceWith())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.S3Action ConvertToSdk(S3Action source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.S3Action.builder()
                .roleArn(source.getRoleArn())
                .s3Bucket(source.getS3Bucket())
                .s3Prefix(source.getS3Prefix())
                .s3SseKmsKeyId(source.getS3SseKmsKeyId())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleCondition ConvertToSdk(RuleCondition source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleCondition.builder()
                .booleanExpression(ConvertToSdk(source.getBooleanExpression()))
                .dmarcExpression(ConvertToSdk(source.getDmarcExpression()))
                .numberExpression(ConvertToSdk(source.getNumberExpression()))
                .stringExpression(ConvertToSdk(source.getStringExpression()))
                .ipExpression(ConvertToSdk(source.getIpExpression()))
                .verdictExpression(ConvertToSdk(source.getVerdictExpression()))
                .build();
    }

    // Expression
    static software.amazon.awssdk.services.mailmanager.model.RuleBooleanExpression ConvertToSdk(RuleBooleanExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleBooleanExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleDmarcExpression ConvertToSdk(RuleDmarcExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleDmarcExpression.builder()
                .operator(source.getOperator())
                .valuesWithStrings(source.getValues())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleNumberExpression ConvertToSdk(RuleNumberExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleNumberExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .value(source.getValue())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleStringExpression ConvertToSdk(RuleStringExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleStringExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .values(source.getValues())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleIpExpression ConvertToSdk(RuleIpExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleIpExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .values(source.getValues())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleVerdictExpression ConvertToSdk(RuleVerdictExpression source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleVerdictExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .valuesWithStrings(source.getValues())
                .build();
    }

    // RuleToEvaluate
    static software.amazon.awssdk.services.mailmanager.model.RuleBooleanToEvaluate ConvertToSdk(RuleBooleanToEvaluate source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleBooleanToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleNumberToEvaluate ConvertToSdk(RuleNumberToEvaluate source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleNumberToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleStringToEvaluate ConvertToSdk(RuleStringToEvaluate source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleStringToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleIpToEvaluate ConvertToSdk(RuleIpToEvaluate source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleIpToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.RuleVerdictToEvaluate ConvertToSdk(RuleVerdictToEvaluate source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.RuleVerdictToEvaluate.builder()
                .attribute(source.getAttribute())
                .analysis(ConvertToSdk(source.getAnalysis()))
                .build();
    }

    // Analysis
    static software.amazon.awssdk.services.mailmanager.model.Analysis ConvertToSdk(Analysis source) {
        if (source == null) {
            return null;
        }

        return software.amazon.awssdk.services.mailmanager.model.Analysis.builder()
                .analyzer(source.getAnalyzer())
                .resultField(source.getResultField())
                .build();
    }
}
