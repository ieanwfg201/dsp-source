#!/bin/sh


destination_central_user=${1} 
destination_central_host=${2}
destination_central_ssh_port=${3} 
outputdirprefix=${4}
outputdirfolder=${5}
outputlogdirprefix=${6}

if [ "$7" = "true" ]; then

ssh rohan@localhost "mkdir -p ${outputdirprefix}"
ssh rohan@localhost "mkdir -p ${outputlogdirprefix}"
rsync -drtv ${outputdirprefix}/${outputdirfolder} --delay-updates --rsh="ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p${destination_central_ssh_port}" -drtv  ${destination_central_user}@${destination_central_ssh_port}:${outputdirprefix}/ --stats 

rsync -drtv ${outputlogdirprefix}/${outputdirfolder} --delay-updates --rsh="ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p${destination_central_ssh_port}" -drtv  ${destination_central_user}@${destination_central_ssh_port}:${outputlogdirprefix}/ --stats 

exit $?

fi
