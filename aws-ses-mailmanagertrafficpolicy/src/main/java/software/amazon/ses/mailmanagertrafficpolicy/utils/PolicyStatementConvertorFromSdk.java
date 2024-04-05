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

public class PolicyStatementConvertorFromSdk {
    public static List<PolicyStatement> ConvertFromSdk(List<software.amazon.awssdk.services.mailmanager.model.PolicyStatement> source) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(PolicyStatementConvertorFromSdk::ConvertFromSdk)
                .collect(Collectors.toList());
    }

    static PolicyStatement ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.PolicyStatement source) {
        if (source == null) {
            return null;
        }
        return PolicyStatement.builder()
                .conditions(
                        source.conditions().stream()
                                .map(PolicyStatementConvertorFromSdk::ConvertFromSdk)
                                .collect(Collectors.toList())
                )
                .action(source.actionAsString())
                .build();
    }

    static PolicyCondition ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.PolicyCondition source) {
        if (source == null) {
            return null;
        }
        return PolicyCondition.builder()
                .booleanExpression(ConvertFromSdk(source.booleanExpression()))
                .ipExpression(ConvertFromSdk(source.ipExpression()))
                .tlsExpression(ConvertFromSdk(source.tlsExpression()))
                .stringExpression(ConvertFromSdk(source.stringExpression()))
                .build();
    }

    static IngressBooleanExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressBooleanExpression source) {
        if (source == null) {
            return null;
        }
        return IngressBooleanExpression.builder()
                .evaluate(ConvertFromSdk(source.evaluate()))
                .operator(source.operatorAsString())
                .build();
    }

    static IngressBooleanToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressBooleanToEvaluate source) {
        if (source == null) {
            return null;
        }
        return IngressBooleanToEvaluate.builder()
                .analysis(ConvertFromSdk(source.analysis()))
                .build();
    }

    static IngressAnalysis ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressAnalysis source) {
        if (source == null) {
            return null;
        }
        return IngressAnalysis.builder()
                .analyzer(source.analyzer())
                .resultField(source.resultField())
                .build();
    }

    static IngressIpv4Expression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressIpv4Expression source) {
        if (source == null) {
            return null;
        }
        return IngressIpv4Expression.builder()
                .values(source.values())
                .operator(source.operatorAsString())
                .evaluate(ConvertFromSdk(source.evaluate()))
                .build();
    }

    static IngressIpToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressIpToEvaluate source) {
        if (source == null) {
            return null;
        }
        return IngressIpToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }

    static IngressTlsProtocolExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolExpression source) {
        if (source == null) {
            return null;
        }
        return IngressTlsProtocolExpression.builder()
                .value(source.valueAsString())
                .operator(source.operatorAsString())
                .evaluate(ConvertFromSdk(source.evaluate()))
                .build();
    }

    static IngressTlsProtocolToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressTlsProtocolToEvaluate source) {
        if (source == null) {
            return null;
        }
        return IngressTlsProtocolToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }

    static IngressStringExpression ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressStringExpression source) {
        if (source == null) {
            return null;
        }
        return IngressStringExpression.builder()
                .values(source.values())
                .operator(source.operatorAsString())
                .evaluate(ConvertFromSdk(source.evaluate()))
                .build();
    }

    static IngressStringToEvaluate ConvertFromSdk(software.amazon.awssdk.services.mailmanager.model.IngressStringToEvaluate source) {
        if (source == null) {
            return null;
        }
        return IngressStringToEvaluate.builder()
                .attribute(source.attributeAsString())
                .build();
    }
}
