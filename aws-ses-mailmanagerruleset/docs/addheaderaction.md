# AWS::SES::MailManagerRuleSet AddHeaderAction

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#headerkey" title="HeaderKey">HeaderKey</a>" : <i>String</i>,
    "<a href="#headervalue" title="HeaderValue">HeaderValue</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#headerkey" title="HeaderKey">HeaderKey</a>: <i>String</i>
<a href="#headervalue" title="HeaderValue">HeaderValue</a>: <i>String</i>
</pre>

## Properties

#### HeaderKey

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[xX]\-[a-zA-Z0-9\-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### HeaderValue

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>128</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
