source_dir = ' /var/data/kritter/51degrees'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "optimad"
source_hostname = "admin.mad.com"
destination_dir = "/var/data/kritter/"
transfer_command = "rsync -drtv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -drtv  %s@%s:%s --stats  %s"
