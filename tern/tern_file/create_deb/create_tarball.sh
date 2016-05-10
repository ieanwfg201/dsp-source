rm kritter-tern-file*
#tar  --transform "s,^,kritter/tern-file/," --exclude "*.pyc" -cvzf kritter-tern-file_1.0.orig.tar.gz ../bin/ ../lib/ ../sql/
tar  --exclude "*.pyc" -cvzf kritter-tern-file_1.0.orig.tar.gz ../bin/ ../lib/ ../sql/ -C kritter-tern-file Makefile
tar  --exclude "*.pyc" -cvzf kritter-tern-file-conf-sample_1.0.orig.tar.gz  ../conf/sample -C kritter-tern-file-conf-sample Makefile
