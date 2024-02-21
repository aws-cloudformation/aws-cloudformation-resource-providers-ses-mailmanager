# AWS::SES::MailManagerRuleSet RuleBooleanExpression

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#evaluate" title="Evaluate">Evaluate</a>" : <i><a href="rulebooleantoevaluate.md">RuleBooleanToEvaluate</a></i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#evaluate" title="Evaluate">Evaluate</a>: <i><a href="rulebooleantoevaluate.md">RuleBooleanToEvaluate</a></i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
</pre>

## Properties

#### Evaluate

_Required_: Yes

_Type_: <a href="rulebooleantoevaluate.md">RuleBooleanToEvaluate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

_Required_: Yes

_Type_: String

_Allowed Values_: <code>IS_TRUE</code> | <code>IS_FALSE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
