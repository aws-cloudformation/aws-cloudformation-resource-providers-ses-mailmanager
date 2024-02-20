# AWS::SES::MailManagerTrafficPolicy IngressTlsProtocolExpression

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#evaluate" title="Evaluate">Evaluate</a>" : <i><a href="ingresstlsprotocoltoevaluate.md">IngressTlsProtocolToEvaluate</a></i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>,
    "<a href="#value" title="Value">Value</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#evaluate" title="Evaluate">Evaluate</a>: <i><a href="ingresstlsprotocoltoevaluate.md">IngressTlsProtocolToEvaluate</a></i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
<a href="#value" title="Value">Value</a>: <i>String</i>
</pre>

## Properties

#### Evaluate

_Required_: Yes

_Type_: <a href="ingresstlsprotocoltoevaluate.md">IngressTlsProtocolToEvaluate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

_Required_: Yes

_Type_: String

_Allowed Values_: <code>MINIMUM_TLS_VERSION</code> | <code>IS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Value

_Required_: Yes

_Type_: String

_Allowed Values_: <code>TLS1_2</code> | <code>TLS1_3</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
