build:
	#whatever commans are needed to compile or configure

install:
	mkdir -p $(DESTDIR)/usr/share/kritter/bidder/traffic_forecasting
	rsync --exclude "*.pyc" -a ../../core $(DESTDIR)/usr/share/kritter/bidder/traffic_forecasting
	mkdir -p $(DESTDIR)/usr/share/kritter/bidder/traffic_forecasting/impl
	rsync --exclude "*.pyc" -a ../../impl/kritter $(DESTDIR)/usr/share/kritter/bidder/traffic_forecasting/impl
	rsync --exclude "*.pyc" -a ../../conf_utils $(DESTDIR)/usr/share/kritter/bidder/traffic_forecasting
	rsync --exclude "*.pyc" -a ../../../utils $(DESTDIR)/usr/share/kritter/bidder
	rsync --exclude "*.pyc" -a ../../../input_log_adapter $(DESTDIR)/usr/share/kritter/bidder
	mkdir -p $(DESTDIR)/usr/share/kritter/utils
	rsync --exclude "*.pyc" -a ../../../../utils/log_configure $(DESTDIR)/usr/share/kritter/utils

