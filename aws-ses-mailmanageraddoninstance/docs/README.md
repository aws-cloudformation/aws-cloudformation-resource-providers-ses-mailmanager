# AWS::SES::MailManagerAddonInstance

Definition of AWS::SES::MailManagerAddonInstance Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerAddonInstance",
    "Properties" : {
        "<a href="#addonsubscriptionid" title="AddonSubscriptionId">AddonSubscriptionId</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerAddonInstance
Properties:
    <a href="#addonsubscriptionid" title="AddonSubscriptionId">AddonSubscriptionId</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### AddonSubscriptionId

_Required_: Yes

_Type_: String

_Minimum Length_: <code>4</code>

_Maximum Length_: <code>67</code>

_Pattern_: <code>^as-[a-zA-Z0-9]{1,64}$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the AddonInstanceId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### AddonInstanceArn

Returns the <code>AddonInstanceArn</code> value.

#### AddonInstanceId

Returns the <code>AddonInstanceId</code> value.

#### AddonName

Returns the <code>AddonName</code> value.
