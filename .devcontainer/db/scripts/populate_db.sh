#!/usr/bin/env sh

# Magenta-specific

echo 'Setting up all databases...'
cd /database/mysql || exit 1
echo 'Creating development database...'
./createdatabase_on.sh root password oscar
echo 'Creating test database...'
./createdatabase_on.sh root password oscar_test
cd ../../
