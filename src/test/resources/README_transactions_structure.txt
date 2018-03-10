# Transaction structure depends on the environment the block chain is used.
# They can be seen as dictionaries with keys and corresponding values.
# In an attempt to make transaction creation as dynamic as possible, the transaction
# keys are read from a configuration file. The type of the value is also given in
# the configuration file, in order to know the valid values for the key's values.

# A transaction has the following structure:
#
# <key>     <variable type>

# Thus far supported types are integer, double, string, long