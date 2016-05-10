#!/bin/bash
if [ "$1" = "" ]; then
    echo "Usage : addnews3bucket.sh <bucketname>"
    exit
fi

mkdir /mnt/$1
if [ "$?" != "0" ]; then
    echo "Bucket already exists"
    exit
fi
/usr/bin/s3fs $1 /mnt/$1
