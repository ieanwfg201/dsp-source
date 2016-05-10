# TernFile Client Config
# The directories should not have trailing /

# SOURCE DIRECTORY#
source_dir = '/var/log/kritter/billing/data'
#SOURCE FILE PATTERN in unix#
source_file_pattern = 'billing-thrift.log.2*'
#SOURCE FILE PATTERN in DB#
source_file_pattern_db = '%billing-thrift.log%'
#DESTINATION HOST PORT#
destination_list = {'azkaban.mad.com' : 22}
#LOG DIRECTORY#
logdir = '/var/log/kritter/tern_file/billing'

# LS COMMAND TO USE#
ls_command = 'ls -1rt'
#FORMAT of DATE FILE TIMESTAMP#
ts_format = '%Y-%m-%d-%H-%M'
#LOG ROTATE TIME INTERVAL in mins#
time_interval = 1
#Specifies the minimum modified time of file before it could be transfered in mins#
min_modified_time = 0
#Command to use for transfer#
command = 'rsync %s --rsh="%s -p%s" %s %s --stats %s %s@%s:%s'
#unix user name#
username = 'ubuntu'
#specifies type of transfer#
transport_type = 'rsync'
#Specifies destination path of transfer#
destination_path = '/var/data/kritter/tern_file/destination/billing'
# specifies timeout in seconds#
timeout = '--timeout=500'
# any other command option to use#
other_option = ''
# Specifies the journal type#
journal_type = 'sqlite'
#Specifies journal path#
sqlite_journal_path = '/var/data/kritter/tern_file/db/billing/tern_journal.db'
#Specifies tablename to be used for journaling#
table_name = 'tern_transfer_state'
#Specifies journal file pattern to use for clean up#
source_date_pattern_for_cleanup = '%billing-thrift.log-%Y-%m-%d'
#specifies journal retention period in days#
retention_period = 4
#The backlog log count on which warning is given in nagios#
backlog_warn_count = 10
#The backlog log count on which critical alert is given in nagios#
backlog_crit_count = 15
#acceptable time lag of file yet to be transfered in seconds#
backlog_time_lag = 900 # in seconds


