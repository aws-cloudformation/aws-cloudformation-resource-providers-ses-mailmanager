# AWS::SES::MailManagerArchive ArchiveRetention

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#retentionperiod" title="RetentionPeriod">RetentionPeriod</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#retentionperiod" title="RetentionPeriod">RetentionPeriod</a>: <i>String</i>
</pre>

## Properties

#### RetentionPeriod

_Required_: Yes

_Type_: String

_Allowed Values_: <code>THREE_MONTHS</code> | <code>SIX_MONTHS</code> | <code>NINE_MONTHS</code> | <code>ONE_YEAR</code> | <code>EIGHTEEN_MONTHS</code> | <code>TWO_YEARS</code> | <code>THIRTY_MONTHS</code> | <code>THREE_YEARS</code> | <code>FOUR_YEARS</code> | <code>FIVE_YEARS</code> | <code>SIX_YEARS</code> | <code>SEVEN_YEARS</code> | <code>EIGHT_YEARS</code> | <code>NINE_YEARS</code> | <code>TEN_YEARS</code> | <code>PERMANENT</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
