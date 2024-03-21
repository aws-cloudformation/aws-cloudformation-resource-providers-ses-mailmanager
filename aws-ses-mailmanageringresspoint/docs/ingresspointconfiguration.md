# AWS::SES::MailManagerIngressPoint IngressPointConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#smtppassword" title="SmtpPassword">SmtpPassword</a>" : <i>String</i>,
    "<a href="#secretarn" title="SecretArn">SecretArn</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#smtppassword" title="SmtpPassword">SmtpPassword</a>: <i>String</i>
<a href="#secretarn" title="SecretArn">SecretArn</a>: <i>String</i>
</pre>

## Properties

#### SmtpPassword

_Required_: Yes

_Type_: String

_Minimum Length_: <code>8</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[A-Za-z0-9!@#$%^&*()_+\-=\[\]{}|.,?]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SecretArn

_Required_: Yes

_Type_: String

_Pattern_: <code>^arn:(aws|aws-cn|aws-us-gov):secretsmanager:[a-z0-9-]+:\d{12}:secret:[a-zA-Z0-9/_+=,.@-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
