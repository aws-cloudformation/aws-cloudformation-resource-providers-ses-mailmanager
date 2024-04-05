# AWS::SES::MailManagerRelay RelayAuthentication

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#passwordauthentication" title="PasswordAuthentication">PasswordAuthentication</a>" : <i><a href="relaypasswordauthentication.md">RelayPasswordAuthentication</a></i>,
    "<a href="#secretarn" title="SecretArn">SecretArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#passwordauthentication" title="PasswordAuthentication">PasswordAuthentication</a>: <i><a href="relaypasswordauthentication.md">RelayPasswordAuthentication</a></i>
<a href="#secretarn" title="SecretArn">SecretArn</a>: <i>String</i>
</pre>

## Properties

#### PasswordAuthentication

_Required_: Yes

_Type_: <a href="relaypasswordauthentication.md">RelayPasswordAuthentication</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SecretArn

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
