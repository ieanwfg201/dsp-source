export JAVA_HOME=/usr/lib/jvm/default-java
export PATH=/usr/lib/jvm/default-java/bin:$PATH
rm -rf ~/externallibs/play/play-2.2.3/repository/cache/com.kritter.*
~/externallibs/play/play-2.2.3/play clean compile
