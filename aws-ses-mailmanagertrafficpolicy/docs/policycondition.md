# AWS::SES::MailManagerTrafficPolicy PolicyCondition

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#stringexpression" title="StringExpression">StringExpression</a>" : <i><a href="ingressstringexpression.md">IngressStringExpression</a></i>,
    "<a href="#ipexpression" title="IpExpression">IpExpression</a>" : <i><a href="ingressipv4expression.md">IngressIpv4Expression</a></i>,
    "<a href="#tlsexpression" title="TlsExpression">TlsExpression</a>" : <i><a href="ingresstlsprotocolexpression.md">IngressTlsProtocolExpression</a></i>,
    "<a href="#booleanexpression" title="BooleanExpression">BooleanExpression</a>" : <i><a href="ingressbooleanexpression.md">IngressBooleanExpression</a></i>
}
</pre>

### YAML

<pre>
<a href="#stringexpression" title="StringExpression">StringExpression</a>: <i><a href="ingressstringexpression.md">IngressStringExpression</a></i>
<a href="#ipexpression" title="IpExpression">IpExpression</a>: <i><a href="ingressipv4expression.md">IngressIpv4Expression</a></i>
<a href="#tlsexpression" title="TlsExpression">TlsExpression</a>: <i><a href="ingresstlsprotocolexpression.md">IngressTlsProtocolExpression</a></i>
<a href="#booleanexpression" title="BooleanExpression">BooleanExpression</a>: <i><a href="ingressbooleanexpression.md">IngressBooleanExpression</a></i>
</pre>

## Properties

#### StringExpression

_Required_: Yes

_Type_: <a href="ingressstringexpression.md">IngressStringExpression</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IpExpression

_Required_: Yes

_Type_: <a href="ingressipv4expression.md">IngressIpv4Expression</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TlsExpression

_Required_: Yes

_Type_: <a href="ingresstlsprotocolexpression.md">IngressTlsProtocolExpression</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BooleanExpression

_Required_: Yes

_Type_: <a href="ingressbooleanexpression.md">IngressBooleanExpression</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
