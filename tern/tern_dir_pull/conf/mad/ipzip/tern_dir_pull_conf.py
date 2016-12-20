source_dir = ' /var/app/kritter/kritterui/kritter-1-0-SNAPSHOT/public/targeting'
symlink_resolver_command = "ssh -o 'StrictHostKeyChecking no' -o 'UserKnownHostsFile=/dev/null' %s@%s 'readlink -f %s'"
source_username = "optimad"
source_hostname = "admin.mad.com"
# azkaban machine destination_dir = "/var/data/kritter/tern_file/destination/useridupload/"
# adserving machine destination_dir = "/var/data/kritter/location/"
destination_dir = "/var/data/kritter/location/"
transfer_command = "rsync -drtv --rsh=\"ssh -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking no' -p22\" -drtv  %s@%s:%s --stats  %s"
