Installation :
To install or update, do the following :

First install thrift :
sudo pip install thrift

a) If the repository is already present on the machine :
    Go to the repo and do 
        python setup.py install
b) If the repository is not present :
    sudo pip install git+https://github.com/hirohanin/ysoserious.git


Usage :

Can be run in the following ways :

To see the adserving logs :
    python -m bidder.kritcat /var/data/.../adserving-thrift.log.2015-05*
    or cat /var/data/.../adserving-thrift.log.2015-05* | python -m bidder.kritcat

To see postimpression logs, pass an extra -p flag to the module :
    python -m bidder.kritcat -p /var/data/.../postimpression-thrift.log.2015-05*
    or cat /var/data/.../postimpression-thrift.log.2015-05* | python -m bidder.kritcat -p

To make this easier, use:
    alias "kritcat"="python -m bidder.kritcat"

    and instead of python -m bidder.kritcat, use kritcat

The usage becomes 

    (for adserving)
        kritcat /var/data/.../adserving-thrift.log.2015-05*
        or cat /var/data/.../adserving-thrift.log.2015-05* | kritcat

    (for postimpression)
        kritcat -p /var/data/.../postimpression-thrift.log.2015-05*
        or cat /var/data/.../postimpression-thrift.log.2015-05* | kritcat -p


Compile:
     mvn -e clean install -Pthrift-0.9 -Pproto-2.4.1
