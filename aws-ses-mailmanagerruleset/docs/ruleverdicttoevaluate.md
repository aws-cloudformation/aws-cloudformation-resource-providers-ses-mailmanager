# AWS::SES::MailManagerRuleSet RuleVerdictToEvaluate

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#attribute" title="Attribute">Attribute</a>" : <i>String</i>,
    "<a href="#analysis" title="Analysis">Analysis</a>" : <i><a href="analysis.md">Analysis</a></i>
}
</pre>

### YAML

<pre>
<a href="#attribute" title="Attribute">Attribute</a>: <i>String</i>
<a href="#analysis" title="Analysis">Analysis</a>: <i><a href="analysis.md">Analysis</a></i>
</pre>

## Properties

#### Attribute

_Required_: Yes

_Type_: String

_Allowed Values_: <code>SPF</code> | <code>DKIM</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Analysis

_Required_: Yes

_Type_: <a href="analysis.md">Analysis</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
