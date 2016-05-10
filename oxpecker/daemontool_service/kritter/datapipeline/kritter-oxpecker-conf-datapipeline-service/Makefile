build:
	#whatever commans are needed to compile or configure

install:
	#whatever commands are needed to 
	# install. it should behave as if 
	# $(DESTDIR) is the target of the install
	rsync -a ../run $(DESTDIR)/etc/kritter-oxpecker-conf-datapipeline-service/
	rsync -a ../log $(DESTDIR)/etc/kritter-oxpecker-conf-datapipeline-service/

