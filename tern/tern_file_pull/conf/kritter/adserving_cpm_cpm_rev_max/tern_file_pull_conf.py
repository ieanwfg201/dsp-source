source_dir = '/var/data/kritter/bidder/offline_bidder/cpm_cpm/rev_max'
source_file_link_name = 'cpm_rev_max.feed'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "rohan"
source_hostname = "localhost"
destination_dir = "/var/data/kritter/bidder/offline_bidder/cpm_cpm/rev_max"
transfer_command = "rsync -ztv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -ztv  %s@%s:%s --stats  %s"
