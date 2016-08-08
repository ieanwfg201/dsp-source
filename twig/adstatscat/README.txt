Installation :
To install or update, do the following :

First install thrift :
sudo pip install thrift

a) If the repository is already present on the machine :
    Go to the repo :
        Go to <repo>/log_readers
        sudo python setup_adstatscat.py install

Usage :

Can be run in the following ways :

To see the ad stats logs :
    python -m adstatscat /var/data/.../<log name>
    or cat /var/data/.../<log name> | python -m adstatscat

To make this easier, use:
    alias "adstatscat"="python -m adstatscat"

    and instead of python -m adstatscat, use adstatscat

