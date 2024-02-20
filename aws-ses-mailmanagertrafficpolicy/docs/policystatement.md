# AWS::SES::MailManagerTrafficPolicy PolicyStatement

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#conditions" title="Conditions">Conditions</a>" : <i>[ <a href="policycondition.md">PolicyCondition</a>, ... ]</i>,
    "<a href="#action" title="Action">Action</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#conditions" title="Conditions">Conditions</a>: <i>
      - <a href="policycondition.md">PolicyCondition</a></i>
<a href="#action" title="Action">Action</a>: <i>String</i>
</pre>

## Properties

#### Conditions

_Required_: Yes

_Type_: List of <a href="policycondition.md">PolicyCondition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Action

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ALLOW</code> | <code>DENY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
