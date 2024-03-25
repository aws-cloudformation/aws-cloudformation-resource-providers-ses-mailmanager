package software.amazon.ses.mailmanagertrafficpolicy.utils;

import software.amazon.ses.mailmanagertrafficpolicy.IngressAnalysis;
import software.amazon.ses.mailmanagertrafficpolicy.IngressBooleanExpression;
import software.amazon.ses.mailmanagertrafficpolicy.IngressBooleanToEvaluate;
import software.amazon.ses.mailmanagertrafficpolicy.IngressIpToEvaluate;
import software.amazon.ses.mailmanagertrafficpolicy.IngressIpv4Expression;
import software.amazon.ses.mailmanagertrafficpolicy.IngressStringExpression;
import software.amazon.ses.mailmanagertrafficpolicy.IngressStringToEvaluate;
import software.amazon.ses.mailmanagertrafficpolicy.IngressTlsProtocolExpression;
import software.amazon.ses.mailmanagertrafficpolicy.IngressTlsProtocolToEvaluate;
import software.amazon.ses.mailmanagertrafficpolicy.PolicyCondition;
import software.amazon.ses.mailmanagertrafficpolicy.PolicyStatement;

import java.util.List;
import java.util.stream.Collectors;

public class PolicyStatementConvertorToSdk {
    public static List<software.amazon.awssdk.services.mailmanager.model.PolicyStatement> ConvertToSdk(List<PolicyStatement> source) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(PolicyStatementConvertorToSdk::ConvertToSdk)
                .collect(Collectors.toList());
    }
    static software.amazon.awssdk.services.mailmanager.model.PolicyStatement ConvertToSdk(PolicyStatement source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.PolicyStatement.builder()
                .conditions(
                        source.getConditions().stream()
                                .map(PolicyStatementConvertorToSdk::ConvertToSdk)
                                .collect(Collectors.toList())
                )
                .action(source.getAction())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.PolicyCondition ConvertToSdk(PolicyCondition source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.PolicyCondition.builder()
                .booleanExpression(ConvertToSdk(source.getBooleanExpression()))
                .ipExpression(ConvertToSdk(source.getIpExpression()))
                .tlsExpression(ConvertToSdk(source.getTlsExpression()))
                .stringExpression(ConvertToSdk(source.getStringExpression()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressBooleanExpression ConvertToSdk(IngressBooleanExpression source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressBooleanExpression.builder()
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .operator(source.getOperator())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressBooleanToEvaluate ConvertToSdk(IngressBooleanToEvaluate source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressBooleanToEvaluate.builder()
                .analysis(ConvertToSdk(source.getAnalysis()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressAnalysis ConvertToSdk(IngressAnalysis source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressAnalysis.builder()
                .analyzer(source.getAnalyzer())
                .resultField(source.getResultField())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressIpv4Expression ConvertToSdk(IngressIpv4Expression source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressIpv4Expression.builder()
                .value(source.getValue())
                .operator(source.getOperator())
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressIpToEvaluate ConvertToSdk(IngressIpToEvaluate source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressIpToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolExpression ConvertToSdk(IngressTlsProtocolExpression source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolExpression.builder()
                .value(source.getValue())
                .operator(source.getOperator())
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolToEvaluate ConvertToSdk(IngressTlsProtocolToEvaluate source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressStringExpression ConvertToSdk(IngressStringExpression source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressStringExpression.builder()
                .value(source.getValue())
                .operator(source.getOperator())
                .evaluate(ConvertToSdk(source.getEvaluate()))
                .build();
    }

    static software.amazon.awssdk.services.mailmanager.model.IngressStringToEvaluate ConvertToSdk(IngressStringToEvaluate source) {
        if (source == null) {
            return null;
        }
        return software.amazon.awssdk.services.mailmanager.model.IngressStringToEvaluate.builder()
                .attribute(source.getAttribute())
                .build();
    }
}
