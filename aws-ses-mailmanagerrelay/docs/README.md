# AWS::SES::MailManagerRelay

Definition of AWS::SES::MailManagerRelay Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerRelay",
    "Properties" : {
        "<a href="#authentication" title="Authentication">Authentication</a>" : <i><a href="relayauthentication.md">RelayAuthentication</a></i>,
        "<a href="#relayname" title="RelayName">RelayName</a>" : <i>String</i>,
        "<a href="#servername" title="ServerName">ServerName</a>" : <i>String</i>,
        "<a href="#serverport" title="ServerPort">ServerPort</a>" : <i>Double</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerRelay
Properties:
    <a href="#authentication" title="Authentication">Authentication</a>: <i><a href="relayauthentication.md">RelayAuthentication</a></i>
    <a href="#relayname" title="RelayName">RelayName</a>: <i>String</i>
    <a href="#servername" title="ServerName">ServerName</a>: <i>String</i>
    <a href="#serverport" title="ServerPort">ServerPort</a>: <i>Double</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### Authentication

_Required_: Yes

_Type_: <a href="relayauthentication.md">RelayAuthentication</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RelayName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>100</code>

_Pattern_: <code>^[\x21-\x7e]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ServerName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>100</code>

_Pattern_: <code>^[\x21-\x7e]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ServerPort

_Required_: Yes

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the RelayId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### RelayARN

Returns the <code>RelayARN</code> value.

#### RelayId

Returns the <code>RelayId</code> value.
