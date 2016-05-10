#!/bin/bash
if [ "$1" = "" ]; then
    echo "Usage : installs3fs.sh <accesskeyid to secret key mapping file> <bucketname>"
    exit
fi
if [ "$2" = "" ]; then
    echo "Usage : installs3fs.sh <accesskeyid to secret key mapping file> <bucketname>"
    exit
fi

apt-get update
apt-get install -y build-essential libfuse-dev exfat-fuse exfat-utils libcurl4-openssl-dev libxml2-dev mime-support
wget http://s3fs.googlecode.com/files/s3fs-1.59.tar.gz
tar zxf s3fs-1.59.tar.gz
cd s3fs-1.59
./configure --prefix=/usr
make
make install
cd ../
cat $1 >> /etc/passwd-s3fs
chmod 640 /etc/passwd-s3fs
mkdir /mnt/$2
if [ "$?" != "0" ]; then
    echo "Bucket already exists"
    exit
fi
/usr/bin/s3fs $2 /mnt/$2
