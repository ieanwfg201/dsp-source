#1/bin/sh

echo "###########################################################"
echo "# This script sets up path as per the expected PROD PATHS #"
echo "###########################################################"

if [ "$1" = "" ]; then
    echo "#######################################################################################"
    echo "# ERROR in UASGE: sh first_time_install.sh  <USER> <GROUP>                            #"
    echo "#                     Example: sh first_time_install.sh rohan rohan                   #"
    echo "#######################################################################################"
    exit
fi

if [ "$2" = "" ]; then
    echo "#######################################################################################"
    echo "# ERROR in UASGE: sh first_time_install.sh  <USER> <GROUP>                            #"
    echo "#                     Example: sh first_time_install.sh rohan rohan                   #"
    echo "#######################################################################################"
    exit
fi


sourcepath=/usr/share/kritter
deploypath=/var/app/kritter
deployname=`date -u +%Y-%m-%d-%H-%M-%S`
fulldeploypath=${deploypath}/${deployname}

echo "mkdir -p ${fulldeploypath}"
mkdir -p ${fulldeploypath}

echo "rsync -av --progress ${sourcepath}/* ${fulldeploypath}/ --exclude src/main/java --exclude src/test/java"
rsync -av --progress ${sourcepath}/* ${fulldeploypath}/ --exclude src/main/java --exclude src/test/java

echo "chown -R ${1}:${2} ${fulldeploypath}"
chown -R ${1}:${2} ${fulldeploypath}

echo "rm /usr/share/kritter"
rm /usr/share/kritter

echo "cd /usr/share"
cd /usr/share

echo "sudo ln -s ${fulldeploypath} kritter"
sudo ln -s ${fulldeploypath} kritter

