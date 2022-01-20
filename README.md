# plumber

```
Multithreaded data processor.
A processing pipeline is built from command line arguments.

Supported steps are:

bounds:<arg>                  Report smallest and largest value
count:<arg>                   Log item counts at every given number of items
csv-parse:<arg>               Deserialize objects from CSV text
csv-print:<arg>               Serialize objects to CSV text
csv-read:<arg>                Read CSV lines from the given file
csv-write:<arg>               Write current value as CSV object to the given file
decode:<arg>                  Decode binary data from a string using the given algorithm
digest:<arg>                  Calculate a message digest using the given algorithm
dump:<arg>                    Dump pipeline state
dynamodb-delete:<arg>         Delete an element from the given DynamoDB table
dynamodb-key:<arg>            Convert item to a DynamoDB key with the specified range key
dynamodb-list:<arg>           List elements from the given DynamoDB table
dynamodb-read:<arg>           Read an element from the given DynamoDB table
dynamodb-write:<arg>          Write an element to the given DynamoDB table
encode:<arg>                  Encode binary data as text using the given algorithm
file-delete:<arg>             Delete a file from the given directory
file-list:<arg>               Read file names from the given directory
file-read:<arg>               Read a file from the given base directory
file-write:<arg>              Write item as file in the given directory
filter:<arg>                  Filter items that do (true) or don't (false) match the previous find:
find:<arg>                    Find matches of the given regular expression, use with filter: or replace:
format:<arg>                  Produces the argument with all occurrences of ${name} replaced by their value
get:<arg>                     Set the current value to the given attribute
group:<arg>                   Log item counts per value at every given number of items
histogram:<arg>               Build a histogram with the given number of buckets
jdbc-delete:<arg>             Delete a row from the given JDBC table
jdbc-list:<arg>               Retrieve rows from the given JDBC table
jdbc-range:<arg>              Determine the actual range of values for the JDBC primaryKey, use with partition:n
jdbc-read:<arg>               Retrieve a row from the given JDBC table
jdbc-write:<arg>              Insert rows into the given JDBC table
json-parse:<arg>              Deserialize objects from JSON text
json-print:<arg>              Serialize objects to JSON text
json-read:<arg>               Read JSON objects from the given file
json-write:<arg>              Write current value as JSON object to the given file
kafka-read:<arg>              Receive messages from the given Kafka topic
kafka-write:<arg>             Send a message to the given Kafka topic
length:<arg>                  Calculate the length of the current value
line-read:<arg>               Read lines from the given file
line-write:<arg>              Write lines to the given file
log:<arg>                     Log the current value
node-del:<arg>                Remove a subtree of a JSON object using the given JSONPath
node-get:<arg>                Extract a subtree of a JSON object using the given JSONPath
node-set:<arg>                Replace a subtree of a JSON object using the given JSONPath
notnull:<arg>                 Keep only items that are not null (true) or null (false)
parallel:<arg>                Execute following steps using the given number of threads
partitions:<arg>              Generate key ranges for n partitions, use with parallel:<n>
rec-del:<arg>                 Remove the given field from the current record
rec-get:<arg>                 Set the given record field as current value
rec-set:<arg>                 Set the given record field to the current value
replace:<arg>                 Replace all matches of a previous find: with the given replacement
retry:<arg>                   Retry the following steps a given number of times on error
s3-delete:<arg>               Delete an object from the given S3 bucket
s3-list:<arg>                 List objects from the given S3 bucket
s3-read:<arg>                 Get an object from the given S3 bucket
s3-write:<arg>                Put an object into the given S3 bucket
set:<arg>                     Set the given attribute to the current value
sqs-delete:<arg>              Delete a message from the given SQS queue
sqs-read:<arg>                Receive messages from the given SQS queue
sqs-write:<arg>               Send a message to the given SQS queue
sum:<arg>                     Log item sum of item sizes every given number of bytes
time:<arg>                    Log item throughput every given number of items
uuid:<arg>                    Generate random UUIDs
value:<arg>                   Produces the given value once
xml-parse:<arg>               Deserialize objects from XML text
xml-print:<arg>               Serialize objects to XML text
xml-read:<arg>                Read XML objects from the given file
xml-write:<arg>               Write current value as XML object to the given file

Supported global options are:

--help                        Show this help
--explain                     Explain resulting plan, don't execute
--requester-pays              Requester pays access to S3 buckets
--assume-role=<arn>           Assume the given IAM role for all S3 operations
--start-after=<key>           Start after the given S3 key
--stop-after=<key>            Stop after the given S3 key
--key-chars=<list>            Use the given list of characters to generate S3 partition keys
--primary-key=<name>          Use the given JDBC column as primary key
--partition-key=<name>        Use the given DynamoDB attribute as partition key
--range-key=<name>            Use the given DynamoDB attribute as range key
--element-name=<name>         Set XML element name to read/write
--root-element-name=<name>    Set XML root element name to wrap output in
--limit=<n>                   Stop after reading n objects (per thread)
--queue-size=<n>              Queue size for items passed between threads
--fetch-size=<n>              Fetch at most this number of items per request
--wait=<n>                    Wait at most this number of seconds for a new message
--follow                      Keep polling for new messages
--reread                      Re-read all messages
```
