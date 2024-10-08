# AWS::SES::MailManagerRuleSet RuleStringExpression

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#evaluate" title="Evaluate">Evaluate</a>" : <i><a href="rulestringtoevaluate.md">RuleStringToEvaluate</a></i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>,
    "<a href="#values" title="Values">Values</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#evaluate" title="Evaluate">Evaluate</a>: <i><a href="rulestringtoevaluate.md">RuleStringToEvaluate</a></i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
<a href="#values" title="Values">Values</a>: <i>
      - String</i>
</pre>

## Properties

#### Evaluate

_Required_: Yes

_Type_: <a href="rulestringtoevaluate.md">RuleStringToEvaluate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

_Required_: Yes

_Type_: String

_Allowed Values_: <code>EQUALS</code> | <code>NOT_EQUALS</code> | <code>STARTS_WITH</code> | <code>ENDS_WITH</code> | <code>CONTAINS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Values

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
