# AWS::SES::MailManagerRuleSet RuleDmarcExpression

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>,
    "<a href="#value" title="Value">Value</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
<a href="#value" title="Value">Value</a>: <i>
      - String</i>
</pre>

## Properties

#### Operator

_Required_: Yes

_Type_: String

_Allowed Values_: <code>EQUALS</code> | <code>NOT_EQUALS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Value

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
