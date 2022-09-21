#!/bin/bash

TESTCOUNT=0
FAILCOUNT=0

function run-test {
	local EXPECTED="${!#}"
	local ARGS=("${@:1:$#-1}")
	local ACTUAL=$(./plumber --profile=quiet "${ARGS[@]}")
	if [ "$ACTUAL" != "$EXPECTED" ]; then
		echo
		echo "Expected" >&2
		echo "  $EXPECTED" >&2
		echo "but got" >&2
		echo "  $ACTUAL" >&2
		echo "for pipeline:" >&2
		for ACTUAL in "${ARGS[@]}"; do
			echo "  $ACTUAL" >&2
		done
		((FAILCOUNT++))
	fi
	((TESTCOUNT++))
	echo -n "."
}

function report-tests {
	echo
	if [ "$FAILCOUNT" != "0" ]; then
		echo "$FAILCOUNT/$TESTCOUNT tests failed."
		exit 1
	else
		echo "All $TESTCOUNT tests were successful."
	fi
}

trap report-tests EXIT

#######################################################################

run-test \
	value:'Hello' set:greeting \
	value:'world' set:name \
	value:'beautiful' set:attr \
	format:'@{greeting}, @{attr} @{name}!' \
	lines-write \
	'Hello, beautiful world!'

run-test \
	value:0123 \
	lines-write \
	value::0123 \
	lines-write \
	'123
0123'

run-test \
	value:John set:firstname \
	value:Doe set:lastname \
	format:'Full name: @{firstname} @{lastname}' \
	lines-write \
	get:firstname \
	lines-write \
	'Full name: John Doe
John'

run-test \
	value:'The quick brown fox' \
	find:'q.*k (\S+)' \
	lines-write \
	'brown'

run-test \
	value:'The quick brown fox' \
	find:'The (\S+) (\S+) (\S+)' \
	replace:'The $2 $1 bear' \
	lines-write \
	'The brown quick bear'

run-test \
	value:2 set:two \
	value:0,1,2,3,4 csv-parse rec-each \
	set:value \
	is-equal:@two \
	filter:false \
	get:value \
	lines-write \
	'0
1
3
4'

run-test \
	value:0 range-set:start \
	value:10 range-set:end \
	range-each:2 \
	lines-write \
	'2
4
6
8
10'

run-test \
	value:"Hello" rec-set:greeting \
	value:"world" rec-set:name \
	value:"beautiful" rec-set:attr \
	get:record csv-print \
	lines-write \
	'Hello,world,beautiful'

run-test \
	value:'{"version":1,"data":{"n":42,"msg":"Hello","read":true}}' \
	json-parse \
	node-get:'data/msg' \
	lines-write \
	'Hello'

run-test \
	value:'{"array":[9, 8, 7, 6]}' \
	json-parse \
	node-each:array \
	lines-write \
	'9
8
7
6'
