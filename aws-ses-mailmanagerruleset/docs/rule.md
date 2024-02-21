# AWS::SES::MailManagerRuleSet Rule

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#name" title="Name">Name</a>" : <i>String</i>,
    "<a href="#conditions" title="Conditions">Conditions</a>" : <i>[ <a href="rulecondition.md">RuleCondition</a>, ... ]</i>,
    "<a href="#unless" title="Unless">Unless</a>" : <i>[ <a href="rulecondition.md">RuleCondition</a>, ... ]</i>,
    "<a href="#actions" title="Actions">Actions</a>" : <i>[ <a href="ruleaction.md">RuleAction</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#name" title="Name">Name</a>: <i>String</i>
<a href="#conditions" title="Conditions">Conditions</a>: <i>
      - <a href="rulecondition.md">RuleCondition</a></i>
<a href="#unless" title="Unless">Unless</a>: <i>
      - <a href="rulecondition.md">RuleCondition</a></i>
<a href="#actions" title="Actions">Actions</a>: <i>
      - <a href="ruleaction.md">RuleAction</a></i>
</pre>

## Properties

#### Name

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>32</code>

_Pattern_: <code>^[a-zA-Z0-9_.-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Conditions

_Required_: No

_Type_: List of <a href="rulecondition.md">RuleCondition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Unless

_Required_: No

_Type_: List of <a href="rulecondition.md">RuleCondition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Actions

_Required_: Yes

_Type_: List of <a href="ruleaction.md">RuleAction</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
