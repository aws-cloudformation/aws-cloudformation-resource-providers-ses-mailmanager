# AWS::SES::MailManagerIngressPoint

Definition of AWS::SES::MailManagerIngressPoint Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerIngressPoint",
    "Properties" : {
        "<a href="#trafficpolicyid" title="TrafficPolicyId">TrafficPolicyId</a>" : <i>String</i>,
        "<a href="#ingresspointconfiguration" title="IngressPointConfiguration">IngressPointConfiguration</a>" : <i><a href="ingresspointconfiguration.md">IngressPointConfiguration</a></i>,
        "<a href="#ingresspointname" title="IngressPointName">IngressPointName</a>" : <i>String</i>,
        "<a href="#rulesetid" title="RuleSetId">RuleSetId</a>" : <i>String</i>,
        "<a href="#type" title="Type">Type</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerIngressPoint
Properties:
    <a href="#trafficpolicyid" title="TrafficPolicyId">TrafficPolicyId</a>: <i>String</i>
    <a href="#ingresspointconfiguration" title="IngressPointConfiguration">IngressPointConfiguration</a>: <i><a href="ingresspointconfiguration.md">IngressPointConfiguration</a></i>
    <a href="#ingresspointname" title="IngressPointName">IngressPointName</a>: <i>String</i>
    <a href="#rulesetid" title="RuleSetId">RuleSetId</a>: <i>String</i>
    <a href="#type" title="Type">Type</a>: <i>String</i>
</pre>

## Properties

#### TrafficPolicyId

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IngressPointConfiguration

_Required_: No

_Type_: <a href="ingresspointconfiguration.md">IngressPointConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IngressPointName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>3</code>

_Maximum Length_: <code>63</code>

_Pattern_: <code>^[A-Za-z0-9_\-]+$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### RuleSetId

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Type

_Required_: Yes

_Type_: String

_Allowed Values_: <code>OPEN_RELAY</code> | <code>AUTH_RELAY</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the IngressPointId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### IngressPointId

Returns the <code>IngressPointId</code> value.

#### IngressPointArn

Returns the <code>IngressPointArn</code> value.

#### IngressPointStatus

Returns the <code>IngressPointStatus</code> value.

#### ARecord

Returns the <code>ARecord</code> value.
