# plumber

```
Multithreaded data processor.
A processing pipeline is built from command line arguments.

Supported steps are:

AWS DynamoDB
  dynamodb-delete:<arg>       Delete an element from the given DynamoDB table
  dynamodb-key:<arg>          Convert item to a DynamoDB key with the specified range key
  dynamodb-list:<arg>         List elements from the given DynamoDB table
  dynamodb-read:<arg>         Read an element from the given DynamoDB table
  dynamodb-write:<arg>        Write an element to the given DynamoDB table
AWS S3
  s3-bulkdelete:<arg>         Deletes multiple objects from the given S3 bucket, use with bulk:<n>
  s3-delete:<arg>             Delete an object from the given S3 bucket
  s3-list:<arg>               List objects from the given S3 bucket
  s3-read:<arg>               Get an object from the given S3 bucket
  s3-write:<arg>              Put an object into the given S3 bucket
AWS SQS
  sqs-bulkdelete:<arg>        Delete multiple messages from the given SQS queue, use with bulk:<n>
  sqs-bulkwrite:<arg>         Send multiple messages to the given SQS queue, use with bulk:<n>
  sqs-delete:<arg>            Delete a message from the given SQS queue
  sqs-read:<arg>              Receive messages from the given SQS queue
  sqs-write:<arg>             Send a message to the given SQS queue
Apache Kafka
  kafka-read:<arg>            Receive messages from the given Kafka topic
  kafka-write:<arg>           Send a message to the given Kafka topic
Attributes
  get:<arg>                   Set the current value to the given attribute
  set:<arg>                   Set the given attribute to the current value
  value:<arg>                 Sets the current value to the given value
CSV
  csv-parse:<arg>             Deserialize objects from CSV text
  csv-print:<arg>             Serialize objects to CSV text
  csv-read:<arg>              Read CSV lines from the given file
  csv-write:<arg>             Write current value as CSV object to the given file
Files
  files-delete:<arg>          Delete files from the given directory
  files-list:<arg>            Read file names from the given directory
  files-read:<arg>            Read files from the given base directory
  files-write:<arg>           Write items as files in the given directory
  lines-read:<arg>            Read lines from the given file
  lines-write:<arg>           Write lines to the given file
Flow control
  bulk:<arg>                  Execute following steps using chunks of items
  delay:<arg>                 Delay following steps by the given number of milliseconds
  notnull:<arg>               Keep only items that are not null (true) or null (false)
  parallel:<arg>              Execute following steps using the given number of threads
  partitions:<arg>            Generate key ranges for n partitions, use with parallel:<n>
  repeat:<arg>                Repeat the following steps a given number of times
  retry:<arg>                 Retry the following steps a given number of times on error
  unbulk:<arg>                Split bulks into separate items again
JDBC
  jdbc-delete:<arg>           Delete a row from the given JDBC table
  jdbc-list:<arg>             Retrieve rows from the given JDBC table
  jdbc-range:<arg>            Determine the actual range of values for the JDBC primaryKey, use with partition:n
  jdbc-read:<arg>             Retrieve a row from the given JDBC table
  jdbc-write:<arg>            Insert rows into the given JDBC table
JSON
  json-parse:<arg>            Deserialize objects from JSON text
  json-print:<arg>            Serialize objects to JSON text
  json-read:<arg>             Read JSON objects from the given file
  json-write:<arg>            Write current value as JSON object to the given file
Logging
  bounds:<arg>                Log smallest and largest value
  count:<arg>                 Log item counts at every given number of items
  dump:<arg>                  Dump raw item contents
  group:<arg>                 Log item counts per value at every given number of items
  histogram:<arg>             Build a histogram with the given number of buckets
  log:<arg>                   Log the current value
  sum:<arg>                   Log item sum of item sizes every given number of bytes
  time:<arg>                  Log item throughput every given number of items
MongoDB
  mongodb-delete:<arg>        Delete a document from the given MongoDB collection
  mongodb-list:<arg>          List documents from the given MongoDB table
  mongodb-read:<arg>          Read a document from the given MongoDB collection
  mongodb-write:<arg>         Insert a document into the given MongoDB collection
Nodes
  node-clear:<arg>            Clear the curent JSON object
  node-del:<arg>              Remove a subtree of a JSON object using the given JSONPath
  node-get:<arg>              Extract a subtree of a JSON object using the given JSONPath
  node-set:<arg>              Replace a subtree of a JSON object using the given JSONPath
  node-sub:<arg>              Replace the current node with one of its sub nodes
Records
  rec-clear:<arg>             Clear the curent record
  rec-del:<arg>               Remove the given field from the current record
  rec-get:<arg>               Set the given record field as current value
  rec-set:<arg>               Set the given record field to the current value
Text
  digest:<arg>                Calculate a message digest using the given algorithm
  find:<arg>                  Find matches of the given regular expression, use with notnull: or replace:
  format:<arg>                Produces the argument with all occurrences of ${name} replaced by their value
  length:<arg>                Calculate the length of the current value
  replace:<arg>               Replace all matches of a previous find: with the given replacement
  text-read:<arg>             Decode binary data from a string using the given algorithm
  text-write:<arg>            Encode binary data as text using the given algorithm
  uuid:<arg>                  Generate random UUIDs
XML
  xml-parse:<arg>             Deserialize objects from XML text
  xml-print:<arg>             Serialize objects to XML text
  xml-read:<arg>              Read XML objects from the given file
  xml-write:<arg>             Write current value as XML object to the given file

Supported global options are:

--help                        Show this help
--explain                     Explain resulting plan, don't execute
--fail-fast                   Fail on first processing error
--log-level=<level>           Set the log level
--profile=quiet               Set log level to WARN and disable start-up banner
--requester-pays              Requester pays access to S3 buckets
--assume-role=<arn>           Assume the given IAM role for all S3 operations
--start-after=<key>           Start after the given key
--stop-after=<key>            Stop after the given key
--start-range=<key>           Start after the given range key
--stop-range=<key>            Stop after the given range key
--key-chars=<list>            Use the given list of characters to generate S3 partition keys
--primary-key=<name>          Use the given JDBC column as primary key
--partition-key=<name>        Use the given DynamoDB attribute as partition key
--range-key=<name>            Use the given DynamoDB attribute as range key
--element-name=<name>         Set XML element name to read/write
--root-element-name=<name>    Set XML root element name to wrap output in
--pretty-print                Pretty print JSON and XML output
--limit=<n>                   Stop after reading n objects (per thread)
--queue-size=<n>              Queue size for items passed between threads
--bulk-size=<n>               Bulk size for steps that process multiple items at once
--wait=<n>                    Wait at most this number of seconds for a new message
--follow                      Keep polling for new messages
--reread                      Re-read all messages

Credentials can be passed via environment variables:

AWS_*                                 Set AWS credentials                                 
PLUMBER_JDBC_DATASOURCE_URL           Set JDBC url
PLUMBER_JDBC_DATASOURCE_USERNAME      Set JDBC user name
PLUMBER_JDBC_DATASOURCE_PASSWORD      Set JDBC password
PLUMBER_MONGODB_CLIENT_URI            Set MongoDB uri
PLUMBER_MONGODB_CLIENT_USERNAME       Set MongoDB user name
PLUMBER_MONGODB_CLIENT_PASSWORD       Set MongoDB password
PLUMBER_MONGODB_CLIENT_SSLROOTCERT    Set MongoDB SSL CA certificate
PLUMBER_KAFKA_CONSUMER_*              Set Kafka consumer config
PLUMBER_KAFKA_PRODUCER_*              Set Kafka producer config
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

## Find and replace

You can match the current value against a regular expression:
```bash
./plumber \
    value:'The quick brown fox' \
    find:'q.*k (\S+)' \
    log
```
This will yield the matched substring (`quick` in this case) as current value and the matched subgroup as attribute `matchedGroup1` with the value `brown`.
To discard items that didn't match the pattern, use `notnull` or `notnull:false` to keep only those.
Replacing the matched substring is possible with the `replace` step that also supports references to groups:

```bash
./plumber \
    value:'The quick brown fox' \
    find:'The (\S+) (\S+) (\S+)' \
    replace:'The $2 $1 bear' \
    log
```

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
    node-get:data/msg \
    log
```

## I/O

The main purpose of a data pipeline is to actually transfer data.
Each supported integration provides one or more of the following steps:

| step       | purpose
|------------|---------
| `*-list`   | List available objects. Usually, this excludes object contents.
| `*-read`   | Fetch object contents by key/name.
| `*-write`  | Store an object.
| `*-delete` | Delete object by key/name.

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
