#!/bin/bash

# FIXME: when a folder is specified, look for index.html and index.php
# FIXME: escape sequences, e.g. %20
# FIXME: check for a bad request (and issue 400 Bad Request)

# FIXME: user should not specify an id when inserting, server should return
#          newly inserted row id
# TODO: test concurent db requests (inserts, deletes)

document_root=`dirname "$0"`
database="$document_root/db.db"

# shell is bad, mkay?
endline1=`/bin/echo -ne "\r"`
endline2=`/bin/echo -ne "\n"`
endline3=`/bin/echo -ne "\r\n"`
endl="$endline1"
tables=""
path=""

read_timeout=60

prepare_db()
{
    # it's not final version, mkay?
    if [ ! -f "$database" ];
    then
        touch $database
        sqlite3 "$database" < "$document_root/db.sql"
        sqlite3 "$database" < "$document_root/db_seed.sql"
    fi
}

table_exists()
{
    local table=$1
    local exists=0

    for t in $tables; do
        if [ "$table" = "$t" ]; then
            local exists=1
            break
        fi
    done

    return $exists
}

is_token_correct()
{
    local token=$1
    local tokens=`sqlite3 "$database" "select token from access_tokens;"`

    for t in $tokens; do
        if [ "$token" = "$t" ]; then
            tables=`sqlite3 "$database" "select tables from access_tokens where token='$token'"`
            return 0
        fi
    done

    return 1
}

is_endline()
{
    [ "$1" = "$endline1" -o "$1" = "$endline2" -o "$1" = "$endline3" ];
    return $?
}

function is_integer()
{
    [ "$1" -eq "$1" ] > /dev/null 2>&1
    return $?
}

get_param_from_query()
{
    local param=$1
    echo $path | grep -o "[?&]$param=[^&]*" | cut -d "=" -f 2
}

log_error()
{
    local msg=`echo $1 | tr -d "\n\r"`
    local now=`date`
    local stripped_path=`echo $path | tr -d "\n\r"`
    echo "[$now] \"$stripped_path\" \"$msg\"" >> "$document_root/error.log"
}

log_actions() 
{
    local msg=`echo $1 | tr -d "\n\r"`
    local now=`date`
    local stripped_path=`echo $path | tr -d "\n\r"`
    echo "[$now] \"$stripped_path\" \"$msg\"" >> "$document_root/action.log"
}

# return codes:
#  0 - success
#  1 - unsupported method
#
# returns:
#   request_method
#   request_path
#   request_protocol
#   request_data
#   request_access_token
parse_request()
{
    local line_number=1
    local read_data=0
    local content_length=0
    request_access_token=""

    while read -t "$read_timeout" line;
    do
        # empty line, end of headers
        if is_endline "$line"; then
            break
        fi

        # first line
        if [ "$line_number" -eq 1 ]; then
            local method=`echo "$line" | cut -d ' ' -f 1`
            path=`echo "$line" | cut -d ' ' -f 2`
            local protocol=`echo "$line" | cut -d ' ' -f 3`

            if [ "$method" = "GET" -o "$method" = "POST" -o "$method" = "DELETE" ]; then
                # assign the values to global variables
                request_method="$method"
                request_path=`echo "$path" | cut -d '?' -f 1`
                request_access_token=`get_param_from_query "token"`
                request_protocol="$protocol"
            else
                # unsupported method
                return 1
            fi

            if [ "$method" = "POST" ]; then
                local read_data=1
            fi
        else
            local header_name=`echo "$line" | cut -d ':' -f 1`
            local header_value=`echo "$line" | cut -d ':' -f 2 | tr -d "\r\n "`

            if [ "$header_name" = "Content-Length" ]; then
                local content_length="$header_value"
            fi
        fi

        local line_number=`expr $line_number + 1`
    done

    if [ "$read_data" -ne 0 ]; then
        read -n "$content_length" -t "$read_timeout" request_data
    fi

    unset line
    return 0
}

parse_insert_data()
{
    #local param="$1"
    #if [ "$param" == "" ]; then
    #    local tokens=0
    #else
    #    local tokens=`echo "$param" | grep -o "|" | wc -l`
    #    local tokens=`expr "$tokens" + 1`
    #fi

    #for i in `seq "$tokens"`; do
    #    if [ "$i" -gt 1 ]; then
    #        echo -n ", "
    #    fi
    #    echo -n "'"
    #    echo -n "$param" | cut -d '|' -f "$i" | tr -d "\r\n"
    #    echo -n "'"
    #done

    echo -n "NULL, '"
    # FIXME: deletes newlines inside fields, not only at the end
    # FIXME: sanitize sql
    echo -n "$1" | cut -d "|" -f 1- --output-delimiter="', '" | tr -d "\r\n"
    echo -n "'"
}

# protocol used
protocol_used="HTTP/1.1"

# server software
server_used="server.sh/0.1"

# status codes
status_200="OK"
status_400="Bad Request"
status_403="Forbidden"
status_404="Not Found"
status_500="Internal Server Error"

# usage:
#     send_response status_code response_content_type response_content
# example:
#     send_response 200 "text/plain" "Hello, World!"
send_response()
{
    local status_code="$1"
    local response_content_type="$2"
    local response_content="$3"

    eval "local status=\"\${status_${status_code}}\""

    # ex: HTTP/1.1 200 OK
    echo "$protocol_used $status_code $status"

    # ex: Header-Name: value, other-value

    # Content-Type header
    if [ "$response_content_type" != "" ]; then
        echo "Content-Type: $response_content_type"
    fi

    echo "Server: $server_used"

    echo

    # send content, if necessary
    if [ "$response_content" != "" ]; then
        # FIXME: dash echo command escapes characters by default
        /bin/echo -E "$response_content"
        echo
    fi
}

#
# sqlite_query "SELECT ..."
#
# returns:
#   result      - text representation of data received
#   result_rows - number of rows received
#
# exit code is 0 when the query was executed sucessfully
#
sqlite_query()
{
    local query="$1"

    result=`sqlite3 "$database" "$query;" 2> /dev/null`
    local exit_code=$?

    if is_endline "$result"; then
        result_rows=0
    else
        result_rows=`echo "$result" | wc -l`
    fi

    return $exit_code
}

prepare_db

if parse_request; then

    if ! is_token_correct "$request_access_token"; then
        send_response 403 "text/plain" "Access forbidden"
        exit 0
    fi

    action=`echo "$request_path" | sed 's/http:\/\///g' | awk -F/ '{print $2}'`
    id=`echo "$request_path" | sed 's/http:\/\///g' | awk -F/ '{print $3}'`

    if [ "$action" = "" ];
    then
        send_response 404 "text/plain" "Bad action"
        exit 0
    fi

    table_exists "$action"
    table_present=$?

    if [ "$table_present" -eq 0 ]; then
        send_response 404 "text/plain" "Table does not exist or access to it isn't granted by access_token"
        exit 0
    fi

    if [ "$request_method" = "GET" ];
    then
        if [ "$action" = "tetris" -a "$id" = "top" ]; then
            query="select * from '$action' order by score desc limit 10"
        elif is_integer "$id"; then
            query="select * from '$action' where id = '$id'"
        else
            query="select * from '$action'"
        fi

        if sqlite_query "$query"; then
            send_response 200 "text/plain" "Rows: ${result_rows}${endl}${result}"
        else
            log_error "DB error [SELECT]"
            send_response 500 "text/plain" "DB error [SELECT]"
        fi
    elif [ "$request_method" = "POST" ];
    then
        values=`parse_insert_data "$request_data"`
        query="insert into '$action' values ($values); select last_insert_rowid()"

        if sqlite_query "$query"; then
            log_actions "INSERTED INTO $action VALUES ($values)"
            send_response 200 "text/plain" "Row id: $result"
        else
            # FIXME: what about a bad request? (missing fields and such)
            log_error "DB ERROR [INSERT]"
            send_response 500 "text/plain" "DB error [INSERT]"
        fi
    elif [ "$request_method" = "DELETE" ]; then
        query="delete from '$action' where id='$id'; select changes()"

        if sqlite_query "$query"; then
            log_actions "DELETED $id FROM $action"
            send_response 200 "text/plain" "Rows deleted: $result"
        else
            log_error "DB error [DELETE]"
            send_response 500 "text/plain" "DB error [DELETE]"
        fi
    else
        log_error "Unsupported request method"
        send_response 400 "text/plain" "Unsupported request method"
    fi
else
    log_error "Bad request"
    send_response 400 "text/plain" "Your request is bad and you should feel bad"
fi
