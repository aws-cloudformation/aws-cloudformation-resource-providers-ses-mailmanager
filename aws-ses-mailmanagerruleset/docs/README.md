# AWS::SES::MailManagerRuleSet

Definition of AWS::SES::MailManagerRuleSet Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerRuleSet",
    "Properties" : {
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#rulesetname" title="RuleSetName">RuleSetName</a>" : <i>String</i>,
        "<a href="#rules" title="Rules">Rules</a>" : <i>[ <a href="rule.md">Rule</a>, ... ]</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerRuleSet
Properties:
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#rulesetname" title="RuleSetName">RuleSetName</a>: <i>String</i>
    <a href="#rules" title="Rules">Rules</a>: <i>
      - <a href="rule.md">Rule</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### Description

_Required_: Yes

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>^[\s)(a-zA-Z0-9_.-]*$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuleSetName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>100</code>

_Pattern_: <code>^[a-zA-Z0-9_.-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Rules

_Required_: Yes

_Type_: List of <a href="rule.md">Rule</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the RuleSetId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### RuleSetARN

Returns the <code>RuleSetARN</code> value.

#### RuleSetId

Returns the <code>RuleSetId</code> value.
