The example files contain some specific examples of blockchain environments.
More specifically, they contain the structure of a transaction and the possible
values that each variable of the transaction can have.

The structure of the example file is as follows:

For string variables:

<variable_name>     <variable_type>     <possible_values>

For a lot of possible values, we start with a value, place a minus sign
afterwards and then write the end value like this:

<start_value>   -   <end_value>

For numeric variables:

<variable_name>     <variable_type>

Numeric values can possibly have a min or max value associated with them. These are not
there to limit the transaction values but for the generator of the random transactions
that will be used to test the blockchain implementation.