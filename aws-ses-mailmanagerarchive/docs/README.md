# AWS::SES::MailManagerArchive

Definition of AWS::SES::MailManagerArchive Resource Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::SES::MailManagerArchive",
    "Properties" : {
        "<a href="#archivename" title="ArchiveName">ArchiveName</a>" : <i>String</i>,
        "<a href="#kmskeyarn" title="KmsKeyArn">KmsKeyArn</a>" : <i>String</i>,
        "<a href="#retention" title="Retention">Retention</a>" : <i><a href="archiveretention.md">ArchiveRetention</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::SES::MailManagerArchive
Properties:
    <a href="#archivename" title="ArchiveName">ArchiveName</a>: <i>String</i>
    <a href="#kmskeyarn" title="KmsKeyArn">KmsKeyArn</a>: <i>String</i>
    <a href="#retention" title="Retention">Retention</a>: <i><a href="archiveretention.md">ArchiveRetention</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### ArchiveName

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### KmsKeyArn

_Required_: No

_Type_: String

_Pattern_: <code>^arn:aws(|-cn|-us-gov):kms:[a-z0-9-]{1,20}:[0-9]{12}:(key|alias)/.+$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Retention

_Required_: No

_Type_: <a href="archiveretention.md">ArchiveRetention</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ArchiveId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ArchiveArn

Returns the <code>ArchiveArn</code> value.

#### ArchiveId

Returns the <code>ArchiveId</code> value.

#### ArchiveState

Returns the <code>ArchiveState</code> value.
