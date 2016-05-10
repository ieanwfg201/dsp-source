#!/bin/bash
# run as sudo
# installation of clp version 1.5. Upgrade when next version comes
apt-get install g++
apt-get install liblapack3gf liblapack-dev libconfig++8 libconfig++8-dev lib32z1-dev libbz2-dev
cd /usr/share/kritter/external_libraries/coin-Clp-1.15
./configure -C
make
make test
make install
