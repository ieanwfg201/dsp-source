source_dir = ' /var/data/kritter/51degrees'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "ubuntu"
source_hostname = "admin.mad.ad"
destination_dir = "/var/data/kritter/"
transfer_command = "rsync -drtv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -drtv  %s@%s:%s --stats  %s"
