source_dir = '/home/rohan/rsynctest/remote/dest'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "rohan"
source_hostname = "localhost"
destination_dir = "/home/rohan/rsynctest/soucer/"
transfer_command = "rsync -drtv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -drtv  %s@%s:%s --stats  %s"
