TERN : Refer <root>/docs/TERN/ if present

Dependency :

python, python-dev, sqlite3

python modules 
    ast
    datetime
    fnmatch
    logging
    logging.config
    math
    os
    random
    shlex
    socket
    sqlite3
    subprocess
    sys
    time
    traceback
daemontools
daemontools-run
lintian
debhelper
devscripts

Introduction Sub Module:
    a) sql - contains local journal schema
    b) lib - contains tern specific libraries
    c) create_deb - debian creator if required
    d) daemontool_service - daemontool service creator
    e) conf - containes configuration
    f) bin - Entry point / the controller/ framework 


USAGE:


a) Setup
    cd docs/SETUP/local_setup.sh
    sudo su
    ./local_setup.sh {with Params}

b) Create Link : /usr/share/kritter to root of repository
        E.g.: ln -s /home/rohan/myrepo /usr/share/kritter
                where in the code structure would be myrepo/tern/

c) Conf
    A sample conf directory has been provided
    cp -r the same conf directory to <ownconf> directory and make appropriate changes in the conf
    
    
d) Daemon Tool Service

    Create ownconf daemontool service
    Create daemon tool
        E.g.
        cd tern_file/daemontool_service/sample/
        ./create_tarball.sh
        cd kritter-tern-file-conf-sample-service
        debuild clean
        debuild -us -uc
    Install the package created
e) Create pwdless ssh key based authorization  between source and destination


ENSURE retention period of source before installing

For Future
Deb Creation:
    The below is not required if usage point a is being followed
    cd create_deb/;
    ./create_tarball.sh;
    Example:
        cd kritter-tern-file
        debuild clean
        debuild -us -uc
    Install the package created


RUN:

a) TO BRING the service DOWN
sudo svc -u <service name>
b) TO BRING the service UP
sudo svc -u <service name>
