# The normal node gets its configuration from a text file
# The text file contains the keys for the interests and after that
# the values the node is interested in
# For numeric values the type is specified (greater,lower)
# For strings just example values are generated, so a typical config
# file looks like this:

<key>   <variable_type> <weight> <interest_type>    <possible values...>

# In addition to the above the normal node gets configured on values
# like the maximum cache size and the time restraint he wants to
# have on the blocks that it caches.
max_cache_size      <some_value> (0 for no limit)
time_restraint      <some_value> (in seconds, 0 for no limit)