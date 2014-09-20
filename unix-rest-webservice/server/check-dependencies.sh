#!/bin/sh

is_installed()
{
    which "$1" > /dev/null
    return $?
}

require()
{
    if is_installed "$1"; then
        echo "$1 is installed"
    else
        echo "$1 is NOT installed" >&2
        echo >&2
        echo 'Please install the application and retry' >&2
        exit 1
    fi
}

echo 'Checking dependencies:'

require 'cut'
require 'awk'
require 'expr'
require 'sqlite3'

echo
echo 'All dependencies satisfied, it should be safe to run the script'
