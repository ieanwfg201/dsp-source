Enable mysql with local-infile

In my.cnf add local-infile as below

[mysqld]
local-infile
[mysql]
local-infile

And then restart mysql 
sudo /etc/init.d/mysql restart
