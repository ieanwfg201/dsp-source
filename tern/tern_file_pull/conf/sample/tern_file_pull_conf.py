source_dir = '/home/rohan/b'
source_file_link_name = 'test2'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "rohan"
source_hostname = "localhost"
destination_dir = "/home/rohan/a"
transfer_command = "rsync -ztv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -ztv  %s@%s:%s --stats  %s"
