#!/bin/sh
if [ "$#" -ne 3 ]; then
        echo "Usage: ./createdatabase_on.sh [database user] [database password] [database name]"
        exit
fi
./createdatabase_generic.sh $@ bc 9