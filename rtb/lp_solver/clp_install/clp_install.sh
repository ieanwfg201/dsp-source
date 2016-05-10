#!/bin/bash
# run as sudo
# installation of clp version 1.5. Upgrade when next version comes
apt-get -y install g++
apt-get -y install liblapack3gf liblapack-dev libconfig++8 libconfig++8-dev lib32z1-dev libbz2-dev
if [ -d /usr/share/kritter/external_libraries/lp_solvers/coin-Clp ]; then
    if [ -L /usr/share/kritter/external_libraries/lp_solvers/coin-Clp ]; then
        rm /usr/share/kritter/external_libraries/lp_solvers/coin-Clp
    else
        rm -rf /usr/share/kritter/external_libraries/lp_solvers/coin-Clp
    fi
fi
mkdir -p /usr/share/kritter/external_libraries/lp_solvers/
cd /usr/share/kritter/external_libraries/lp_solvers/
svn co https://projects.coin-or.org/svn/Clp/stable/1.15 coin-Clp
cd coin-Clp
./configure -C
make
make test
make install
