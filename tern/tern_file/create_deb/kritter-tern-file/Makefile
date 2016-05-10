build:
	#whatever commans are needed to compile or configure

install:
	#whatever commands are needed to 
	# install. it should behave as if 
	# $(DESTDIR) is the target of the install
	rsync --exclude "*.pyc" -a ../../bin $(DESTDIR)/usr/share/kritter/tern_file
	rsync --exclude "*.pyc" -a ../../lib $(DESTDIR)/usr/share/kritter/tern_file
	rsync --exclude "*.pyc" -a ../../sql $(DESTDIR)/usr/share/kritter/tern_file
	#cp -r ../../bin/ $(DESTDIR)/kritter/tern_file
	#cp -r ../../lib/ $(DESTDIR)/kritter/tern_file
	#cp -r ../../sql/ $(DESTDIR)/kritter/tern_file

