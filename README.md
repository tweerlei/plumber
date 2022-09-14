# plumber

```
Multithreaded data processor.
A processing pipeline is built from command line arguments.

Supported steps and default arguments (if any) are:

AWS DynamoDB
  dynamodb-delete:<table>     Delete an element from the given DynamoDB table
  dynamodb-key:<value>        Convert item to a DynamoDB key with the specified range key
  dynamodb-list:<table>       List elements from the given DynamoDB table
  dynamodb-read:<table>       Read an element from the given DynamoDB table
  dynamodb-write:<table>      Write an element to the given DynamoDB table
AWS S3
  s3-bulkdelete:<bucket>      Deletes multiple objects from the given S3 bucket, use with bulk:<n>
  s3-delete:<bucket>          Delete an object from the given S3 bucket
  s3-list:<bucket>            List objects from the given S3 bucket
  s3-read:<bucket>            Get an object from the given S3 bucket
  s3-write:<bucket>           Put an object into the given S3 bucket
AWS SQS
  sqs-bulkdelete:<queue>      Delete multiple messages from the given SQS queue, use with bulk:<n>
  sqs-bulkwrite:<queue>       Send multiple messages to the given SQS queue, use with bulk:<n>
  sqs-delete:<queue>          Delete a message from the given SQS queue
  sqs-read:<queue>            Receive messages from the given SQS queue
  sqs-write:<queue>           Send a message to the given SQS queue
Apache Kafka
  kafka-read:<topic>          Receive messages from the given Kafka topic
  kafka-write:<topic>         Send a message to the given Kafka topic
Attributes
  and:<value>                 Logically AND the current value with the given attribute's value
  else:<value>                Sets the current value to the given attribute's value if a previous then: did not match
  get:<name>                  Set the current value to the given attribute
  is-equal:<value>            Compare the current value to the given attribute's value resulting in a boolean
  is-greater:<value>          Compare the current value to the given attribute's value resulting in a boolean
  is-less:<value>             Compare the current value to the given attribute's value resulting in a boolean
  not                         Logically negate the current value
  or:<value>                  Logically OR the current value with the given attribute's value
  set:<name>                  Set the given attribute to the current value
  then:<value>                Sets the current value to the given attribute's value if current value is truthy
  value:<value>               Sets the current value to the given value
  xor:<value>                 Logically XOR the current value with the given attribute's value
CSV
  csv-parse                   Deserialize objects from CSV text
  csv-print                   Serialize objects to CSV text
  csv-read:<path>             Read CSV lines from the given file
  csv-write:<path>            Write current value as CSV object to the given file
Files
  files-delete:<path>         Delete files from the given directory
  files-list:<path>           Read file names from the given directory
  files-read:<path>           Read files from the given base directory
  files-write:<path>          Write items as files in the given directory
  lines-read:<path>           Read lines from the given file
  lines-write:<path>          Write lines to the given file
Flow control
  bulk:<number>               Execute following steps using chunks of items
  delay:0                     Delay following steps by the given number of milliseconds
  filter:true                 Keep only items that evaluate to the given boolean
  parallel:8                  Execute following steps using the given number of threads
  repeat:9223372036854775807  Repeat the following steps a given number of times
  retry:9223372036854775807   Retry the following steps a given number of times on error
  unbulk                      Split bulks into separate items again
JDBC
  jdbc-delete:<table>         Delete a row from the given JDBC table
  jdbc-list:<table>           Retrieve rows from the given JDBC table
  jdbc-range:<table>          Determine the actual range of values for the JDBC primaryKey, use with partition:n
  jdbc-read:<table>           Retrieve a row from the given JDBC table
  jdbc-write:<table>          Insert rows into the given JDBC table
JSON
  json-parse                  Deserialize objects from JSON text
  json-print                  Serialize objects to JSON text
  json-read:<path>            Read JSON objects from the given file
  json-write:<path>           Write current value as JSON object to the given file
Logging
  bounds                      Log smallest and largest value
  count:9223372036854775807   Log item counts at every given number of items
  dump                        Dump raw item contents
  error:1                     Throw an error every given number of items
  group:9223372036854775807   Log item counts per value at every given number of items
  histogram:10                Build a histogram with the given number of buckets
  log                         Log the current value
  sum:9223372036854775807     Log item sum of item sizes every given number of bytes
  time:9223372036854775807    Log item throughput every given number of items
MongoDB
  mongodb-delete:<collection> Delete a document from the given MongoDB collection
  mongodb-list:<collection>   List documents from the given MongoDB table
  mongodb-read:<collection>   Read a document from the given MongoDB collection
  mongodb-write:<collection>  Insert a document into the given MongoDB collection
Nodes
  node-clear                  Clear the curent JSON object
  node-del:<path>             Remove a subtree of a JSON object using the given JSONPath
  node-each:<path>            Extract elements from a subtree of a JSON object using the given JSONPath
  node-get:<path>             Extract a subtree of a JSON object using the given JSONPath
  node-set:<path>             Replace a subtree of a JSON object using the given JSONPath
  node-sub:<path>             Replace the current node with one of its sub nodes
Ranges
  is-inrange                  Compare the current value to the current range resulting in a boolean
  partitions:8                Generate key ranges for n partitions, use with parallel:<n>
  range-clear                 Clear the curent range object
  range-each:1                Generate items with the values of the input item's range using the given increment
  range-get:start             Get a range field, one of (start, end)
  range-set:start             Set a range field, e.g. for usage with each:, one of (start, end)
Records
  rec-clear                   Clear the curent record
  rec-del:<name>              Remove the given field from the current record
  rec-each                    Extract record elements to individual items
  rec-get:<name>              Set the given record field as current value
  rec-set:<name>              Set the given record field to the current value
Text
  digest:sha1                 Calculate a message digest using the given algorithm
  find:<regex>                Find matches of the given regular expression, use with notnull: or replace:
  format:<format>             Produces the argument with all occurrences of ${name} replaced by their value
  length                      Calculate the length of the current value
  replace:<value>             Replace all matches of a previous find: with the given replacement
  text-read:hex               Decode binary data from a string using the given algorithm
  text-write:hex              Encode binary data as text using the given algorithm
  uuid                        Generate random UUIDs
XML
  xml-parse                   Deserialize objects from XML text
  xml-print                   Serialize objects to XML text
  xml-read:<path>             Read XML objects from the given file
  xml-write:<path>            Write current value as XML object to the given file

Supported global options and their defaults (if any) are:

--help                        Show this help
--log-level=INFO              Set the log level
--profile=default             Use 'quiet' to disable start-up banner and log only warnings and errors

--explain                     Explain resulting plan, don't execute
--bulk-size=1000              Bulk size for steps that process multiple items at once
--limit=9223372036854775807   Stop after reading n objects (per thread, default is unlimited)
--fail-fast                   Fail on first processing error
--retry-delay=0               Wait this number of seconds before retrying failed messages
--queue-size=10               Queue size for items passed between threads
--primary-key=<arg>           Use the given attribute as primary key (defaults to 'id' for JDBC and '_id' for MongoDB)
--select=[]                   Database fields to fetch, separated by commas
--start-after=<arg>           Start after the given key
--stop-after=<arg>            Stop after the given key
--key-chars=<arg>             Use the given characters to generate keys (defaults to safe S3 chars)
--assume-role=<arg>           AWS; Assume the given IAM role for all AWS operations
--requester-pays              AWS: Requester pays access to S3 buckets
--wait=1                      Kafka/SQS: Wait at most this number of seconds for a new message
--follow                      Kafka/SQS: Keep polling for new messages
--reread                      Kafka/SQS: Re-read all messages
--partition-key=<arg>         DynamoDB: Use the given attribute as partition key
--range-key=<arg>             DynamoDB: Use the given attribute as range key
--start-range=<arg>           DynamoDB: Start after the given range key
--stop-range=<arg>            DynamoDB: Stop after the given range key
--separator=,                 CSV: Separator character
--header                      CSV: Read/write header
--pretty-print                Pretty print JSON and XML output
--element-name=<arg>          XML: Element name to read/write
--root-element-name=<arg>     XML: Root element name to wrap output in

Credentials can be passed via environment variables:

AWS_*                                          Set AWS credentials
PLUMBER_JDBC_DATASOURCE_DRIVERCLASSNAME        Set JDBC driver class name
PLUMBER_JDBC_DATASOURCE_URL                    Set JDBC url
PLUMBER_JDBC_DATASOURCE_USERNAME               Set JDBC user name
PLUMBER_JDBC_DATASOURCE_PASSWORD               Set JDBC password
PLUMBER_MONGODB_CLIENT_URI                     Set MongoDB uri
PLUMBER_MONGODB_CLIENT_USERNAME                Set MongoDB user name
PLUMBER_MONGODB_CLIENT_PASSWORD                Set MongoDB password
PLUMBER_MONGODB_CLIENT_DATABASE                Set MongoDB database
PLUMBER_MONGODB_CLIENT_AUTHENTICATIONDATABASE  Set MongoDB authentication database
PLUMBER_MONGODB_CLIENT_SSLROOTCERT             Set MongoDB SSL CA certificate
PLUMBER_KAFKA_CONSUMER_*                       Set Kafka consumer config
PLUMBER_KAFKA_PRODUCER_*                       Set Kafka producer config
```

## How it works

Each step declared on the command line processes items that are generated by the previous step.
The first step will receive an empty item and the number of items that pass through the last step are counted.

But what is an item? Essentially, it consists of a "current value" that most steps act upon and a number of additional named attributes.
The `file-list` step, for example, will generate items where the "current value" is the name of the file and additional attributes are `name`, `size`, `lastModified`, `filePath` and `fileName`.

Each step knows which attributes it expects and what kind of "current value" it is able to process, so you won't be able to chain steps in an order that doesn't make sense.

How do you find out about the types and attributes handled by each step? Use the `--explain` option to see what's going on.

## Examples

An empty pipeline is not allowed, so the simplest pipeline has one step:
```bash
./plumber log
```
This will simply log the contents of the received work item(s) which in this case will be empty.

A slightly more useful example that still has no real input nor output looks like this:
```bash
./plumber value:'Hello, world!' log
```

You can also use attributes to handle more than one value:
```bash
./plumber \
    value:'Hello' set:greeting \
    value:'world' set:name \
    value:'beautiful' set:attr \
    format:'${greeting}, ${attr} ${name}!' \
    log
```
While the current value gets overwritten by each `value` step, you can save each one to a named attribute and use those to compose a longer value.
A simpler alternative would be to just restore a single attribute value using `get:greeting`.

For debugging purposes, you can also use `dump` instead of `log` to dump all the contents of the item received by that step.

## I/O

The main purpose of a data pipeline is to actually transfer data.
Each supported integration provides one or more of the following steps:

| step       | purpose                                                         |
|------------|-----------------------------------------------------------------|
| `*-list`   | List available objects. Usually, this excludes object contents. |
| `*-read`   | Fetch object contents by key/name.                              |
| `*-write`  | Store an object.                                                |
| `*-delete` | Delete object by key/name.                                      |

Send all text files from /data to an S3 bucket:
```bash
./plumber \
    file-list:/data \
    find:'\.txt$' filter \
    file-read \
    s3-write:mybucket
```

Since the main focus is to transfer large amounts of items, reading a single item is a bit more complicated:
```bash
./plumber \
    value:"filename.txt" set:name \
    file-read:"/path/to/files" \
    s3-write:mybucket
```

Feel free to sprinkle your pipeline with statistics steps to count the items passing through:
```bash
./plumber \
    file-list:/data \
    count:100 find:'\.txt$' filter \
    count:100 file-read \
    time:100 s3-write:mybucket
```

The `count` step doesn't modify items but keeps track of how many items have passed and prints that number every 100 items (in this case). The `time` step will measure the average time it takes to process the next and following steps.

## Bulk operations

Some I/O operations are faster when multiple items can be processed at once.
Input operations like `s3-list` will use bulk mode by default with a limit of 1000 (adjustable via `--bulk-size`). Received items will then be processed individually.
Currently, output operations usually work on single items. There are special steps that support bulk mode like `s3-bulkdelete`. To make use of them, items have to be bulked:
```bash
./plumber \
    s3-list:mybucket \
    bulk:100 \
    s3-bulkdelete
```
If you want to chain "normal" steps to bulk steps, you will have to `unbulk` the items again.

## Parallel processing

Every step can be parallelized using a number of threads. This is quite useful for slow backends like S3 where you can employ 100 threads to fetch contents:
```bash
./plumber \
    s3-list:mybucket \
    parallel:100 s3-read \
    file-write:/backup
```

In the last example, all the files in the bucket are listed sequentially.
If you know the key space, you can also instruct the `*-list` steps to work on partitions of keys:
```bash
./plumber \
    partitions:20 --start-after=200 --stop-after=400 --key-chars=0123456789abcdef \
    parallel:4 s3-list:mybucket \
    lines-write:filenames.txt
```
This will generate 20 partitions for the key space between 200 (exclusive) and 400 (inclusive) that will be sent to 4 parallel threads that will list the files in each partition, continuing with the next partition when done.
Notice that the last step `line-write` will be forced to run in a single thread to prevent overlapping writes to filenames.txt

## Values

Values given on the command line (e.g. via `value:`) are automatically converted to an appropriate internal type:

| Input                | Type    | Output               |
|----------------------|---------|----------------------|
| null                 | null    |                      |
| true                 | Boolean | true                 |
| false                | Boolean | false                |
| 042                  | Long    | 42                   |
| 3.14                 | Double  | 3.14                 |
| 3e2                  | Double  | 300                  |
| 2022-03-14T15:30:00Z | Instant | 2022-03-14T15:30:00Z |

If you don't want that, use two colons:

```bash
./plumber \
    value:0123 \
    log \
    value::0123 \
    log
```

## Find and replace

You can match the current value against a regular expression:
```bash
./plumber \
    value:'The quick brown fox' \
    find:'q.*k (\S+)' \
    log
```
This will yield the matched substring (`quick` in this case) as current value and the matched subgroup as attribute `matchedGroup1` with the value `brown`.
To discard items that did or did not match the pattern, use `is-equal:null filter:false` (see below).
Replacing the matched substring is possible with the `replace` step that also supports references to groups:

```bash
./plumber \
    value:'The quick brown fox' \
    find:'The (\S+) (\S+) (\S+)' \
    replace:'The $2 $1 bear' \
    log
```

## Logic and filtering

Values can be compared and results can be combined (also with other boolean attributes) using logical operators:

```bash
./plumber \
    value:true set:yes \
    value:false set:no \
    value:true \
    and:no \
    or:yes \
    not \
    log
```

This is the verbose equivalent of `not ((true and false) or true)`.

```bash
./plumber \
    value:Hello set:abc \
    value:World set:def \
    value:42 set:limit \
    value:10 is-greater:limit \
    not \
    then:abc else:def \
    log
```

This is the verbose equivalent of `if not (10 > 42) then 'Hello' else 'World'`.

Items can then be filtered to keep only those that evaluate to true (default) or false.
This evaluation also applies to string values, so an empty string, as well as '0' and 'false' will evaluate to false.
If you want to evaluate 'false' to false, use an explicit `is-equal:`.

```bash
./plumber \
    value:2 set:two \
    value:0,1,2,3,4 csv-parse record-each \
    set:value \
    is-equal:two \
    filter:false \
    get:value \
    log
```

## Ranges

Ranges are used for example to specify bounds when querying input sources:

```bash
./plumber \
    jdbc-range:mytable --primary-key=id \
    jdbc-read \
    log
```

Here, the `jdbc-range` step creates a range from the minimum and maximum values of the `id` column and passes that to the `jdbc-read` step, which then will use it to create its SELECT query.
You can build a range manually as well:

```bash
./plumber \
    value:0 range-set:start \
    value:100 range-set:end \
    jdbc-read:mytable --primary-key=id \
    log
```

Note that a range **excludes** the start element, so this will SELECT all rows where the `id` is between 1 and 10.
If the range has integer bounds, it is also possible to iterate over elements using a given step:

```bash
./plumber \
    value:0 range-set:start \
    value:100 range-set:end \
    range-each:2 \
    log
```

This will produce items `2`, `4`, `6`, `8` and `10`.

## Records

Sometimes a single "current value" is not enough. When you read a row from a database or parse a CSV line, the result will have multiple columns. Such results will usually be stored in a magic attribute named `record`.
The magic happens when you use the special steps `rec-set`, `rec-get` and `rec-del`. Those will set, get or remove fields from the record, which can then be serialized or written to another DB table:
```bash
./plumber \
    value:"Hello" rec-set:greeting \
    value:"world" rec-set:name \
    value:"beautiful" rec-set:attr \
    get:record csv-print \
    log
```
(Notice that `csv-print` will actually expect the record as current value, so you have to to `get:record` first.)

## Nodes

For more complex data structures like JSON or XML, the parsed result is stored as a tree of nodes with the magic name `node` that can be modified using special steps
`node-set`, `node-get` and `node-del` that expect path-style addressing:
```bash
./plumber \
    value:'{"version":1,"data":{"n":42,"msg":"Hello","read":true}}' \
    json-parse \
    node-get:'data/msg' \
    log
```

JSON arrays can be extracted into separate items:

```bash
./plumber \
    value:'{"array":[9, 8, 7, 6]}' \
    json-parse \
    node-each:array \
    log
```
