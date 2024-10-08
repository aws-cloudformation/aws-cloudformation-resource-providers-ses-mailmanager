# AWS::SES::MailManagerRuleSet RuleStringToEvaluate

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#attribute" title="Attribute">Attribute</a>" : <i>String</i>,
    "<a href="#mimeheaderattribute" title="MimeHeaderAttribute">MimeHeaderAttribute</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#attribute" title="Attribute">Attribute</a>: <i>String</i>
<a href="#mimeheaderattribute" title="MimeHeaderAttribute">MimeHeaderAttribute</a>: <i>String</i>
</pre>

## Properties

#### Attribute

_Required_: Yes

_Type_: String

_Allowed Values_: <code>MAIL_FROM</code> | <code>HELO</code> | <code>RECIPIENT</code> | <code>SENDER</code> | <code>FROM</code> | <code>SUBJECT</code> | <code>TO</code> | <code>CC</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MimeHeaderAttribute

_Required_: Yes

_Type_: String

_Pattern_: <code>^X-[a-zA-Z0-9-]{1,256}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
