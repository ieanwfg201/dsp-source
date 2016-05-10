#!/bin/sh
rm files.txt
find . | grep html$ | sort -r > files.txt
allfiles=`tr '\n' ' ' < files.txt`
exec htmldoc --webpage -f  pdf-file.pdf ${allfiles}
