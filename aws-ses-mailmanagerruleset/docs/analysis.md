# AWS::SES::MailManagerRuleSet Analysis

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#analyzer" title="Analyzer">Analyzer</a>" : <i>String</i>,
    "<a href="#resultfield" title="ResultField">ResultField</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#analyzer" title="Analyzer">Analyzer</a>: <i>String</i>
<a href="#resultfield" title="ResultField">ResultField</a>: <i>String</i>
</pre>

## Properties

#### Analyzer

_Required_: Yes

_Type_: String

_Pattern_: <code>^[a-zA-Z0-9:_/+=,@.#-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResultField

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>256</code>

_Pattern_: <code>^[\sa-zA-Z0-9_]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
