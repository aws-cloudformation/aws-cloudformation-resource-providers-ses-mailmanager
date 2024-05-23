# AWS::SES::MailManagerRelay RelayAuthentication

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#secretarn" title="SecretArn">SecretArn</a>" : <i>String</i>,
    "<a href="#noauthentication" title="NoAuthentication">NoAuthentication</a>" : <i>Map</i>
}
</pre>

### YAML

<pre>
<a href="#secretarn" title="SecretArn">SecretArn</a>: <i>String</i>
<a href="#noauthentication" title="NoAuthentication">NoAuthentication</a>: <i>Map</i>
</pre>

## Properties

#### SecretArn

_Required_: Yes

_Type_: String

_Pattern_: <code>^arn:(aws|aws-cn|aws-us-gov):secretsmanager:[a-z0-9-]+:\d{12}:secret:[a-zA-Z0-9/_+=,.@-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### NoAuthentication

_Required_: Yes

_Type_: Map

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
