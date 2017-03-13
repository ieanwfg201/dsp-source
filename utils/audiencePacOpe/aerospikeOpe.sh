#!/bin/bash
echo "Please input the absolute path of the Audience Package"
read filepath
echo "The operation file is:"
echo "$filepath"
echo "The audienceid is"
read audienceid


echo "Continue? yes/no"
read judge
echo ""

if [ $judge = yes ]
then

#preprocessing audience package
for line in `cat $filepath`
do
pk=${line%:*}

#create aql commands
aql="insert into user.audience_package(PK,PK,audience_id) values('$pk','$pk','$audienceid')"

#aql commands output to aqlCommands
echo $aql >> aqlCommands

done
fi

#execute aql commands
aql -f aqlCommands

echo "=============================="
echo "******Operation Success!******"
echo "=============================="

#clean aqlCommands for next operation
echo > aqlCommands

