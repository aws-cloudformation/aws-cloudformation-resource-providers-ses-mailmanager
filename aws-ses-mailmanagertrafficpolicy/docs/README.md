# AWS::SES::MailManagerTrafficPolicy

Definition of AWS::SES::MailManagerTrafficPolicy Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerTrafficPolicy",
    "Properties" : {
        "<a href="#defaultaction" title="DefaultAction">DefaultAction</a>" : <i>String</i>,
        "<a href="#maxmessagesizebytes" title="MaxMessageSizeBytes">MaxMessageSizeBytes</a>" : <i>Double</i>,
        "<a href="#policystatements" title="PolicyStatements">PolicyStatements</a>" : <i>[ <a href="policystatement.md">PolicyStatement</a>, ... ]</i>,
        "<a href="#trafficpolicyname" title="TrafficPolicyName">TrafficPolicyName</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerTrafficPolicy
Properties:
    <a href="#defaultaction" title="DefaultAction">DefaultAction</a>: <i>String</i>
    <a href="#maxmessagesizebytes" title="MaxMessageSizeBytes">MaxMessageSizeBytes</a>: <i>Double</i>
    <a href="#policystatements" title="PolicyStatements">PolicyStatements</a>: <i>
      - <a href="policystatement.md">PolicyStatement</a></i>
    <a href="#trafficpolicyname" title="TrafficPolicyName">TrafficPolicyName</a>: <i>String</i>
</pre>

## Properties

#### DefaultAction

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ALLOW</code> | <code>DENY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxMessageSizeBytes

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PolicyStatements

_Required_: Yes

_Type_: List of <a href="policystatement.md">PolicyStatement</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TrafficPolicyName

_Required_: No

_Type_: String

_Minimum Length_: <code>3</code>

_Maximum Length_: <code>63</code>

_Pattern_: <code>^[A-Za-z0-9_\-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the TrafficPolicyId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TrafficPolicyArn

Returns the <code>TrafficPolicyArn</code> value.

#### TrafficPolicyId

Returns the <code>TrafficPolicyId</code> value.
