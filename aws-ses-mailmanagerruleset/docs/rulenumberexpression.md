# AWS::SES::MailManagerRuleSet RuleNumberExpression

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#evaluate" title="Evaluate">Evaluate</a>" : <i><a href="rulenumbertoevaluate.md">RuleNumberToEvaluate</a></i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>,
    "<a href="#value" title="Value">Value</a>" : <i>Double</i>
}
</pre>

### YAML

<pre>
<a href="#evaluate" title="Evaluate">Evaluate</a>: <i><a href="rulenumbertoevaluate.md">RuleNumberToEvaluate</a></i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
<a href="#value" title="Value">Value</a>: <i>Double</i>
</pre>

## Properties

#### Evaluate

_Required_: Yes

_Type_: <a href="rulenumbertoevaluate.md">RuleNumberToEvaluate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

_Required_: Yes

_Type_: String

_Allowed Values_: <code>EQUALS</code> | <code>NOT_EQUALS</code> | <code>LESS_THAN</code> | <code>GREATER_THAN</code> | <code>LESS_THAN_OR_EQUAL</code> | <code>GREATER_THAN_OR_EQUAL</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Value

_Required_: Yes

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
