build:
	#whatever commans are needed to compile or configure

install:
	#whatever commands are needed to 
	# install. it should behave as if 
	# $(DESTDIR) is the target of the install
	rsync --exclude "*.pyc" -a ../../conf $(DESTDIR)/usr/share/kritter/tern_file

