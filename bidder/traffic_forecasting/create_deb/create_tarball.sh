rm kritter-traffic-forecasting*
rm -rf ../../input_log_adapter/gen-py
thrift -gen py -o ../../input_log_adapter/ ../../../data_structs/src/main/thrift/AdservingRequestResponse.thrift
tar  --exclude "*.pyc" -cvzf kritter-traffic-forecasting_1.0.orig.tar.gz ../core ../impl/kritter ../conf_utils ../../utils ../../input_log_adapter ../../../utils/log_configure -C kritter-traffic-forecasting Makefile
tar  --exclude "*.pyc" -cvzf kritter-traffic-forecasting-conf-sample_1.0.orig.tar.gz  ../sample/config -C kritter-traffic-forecasting-conf-sample Makefile
