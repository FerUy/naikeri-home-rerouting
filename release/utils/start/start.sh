#!/bin/bash
# Starts Naikeri HRR with one of the configurations in ../conf, named by the first argument:
#   bin/start.sh cap-proxy.xml   the CAP proxy
#   bin/start.sh map-proxy.xml   the MAP proxy
# JAVA_OPTS, if set, replaces the default heap settings.
cd "$(dirname "$0")" || exit 1

if [ $# -lt 1 ]; then
  echo "Usage: $0 <configuration file in conf/>" >&2
  echo "Configurations available:" >&2
  ls ../conf | grep -E -- '-proxy\.xml$' | sed 's/^/  /' >&2
  exit 1
fi
CONFIG="$1"
if [ ! -f "../conf/$CONFIG" ]; then
  echo "No configuration '$CONFIG' in $(cd ../conf && pwd)" >&2
  exit 1
fi

trap 'kill -TERM $PID' TERM INT
mkdir -p ../logs
# Console output goes to the terminal and to a file. Anything logged before log4j2.xml is read, and any
# log4j2 configuration error, appears only here. tee runs in a process substitution so that $! below is
# still the JVM's PID and the traps keep working.
CONSOLE_LOG=../logs/console.out

java ${JAVA_OPTS:--Xms1g -Xmx1g} -cp home-re-routing-VERSION.jar:lib/* \
  -Dlog4j2.configurationFile=../conf/log4j2.xml -Dorg.restcomm.sctp.bufferSize=50000000 -DmainConfig.path=../conf -Dhrr.log.dir=../logs \
  com.naikeri.hrr.impl.HomeReRouting "$CONFIG" > >(tee -a "$CONSOLE_LOG") 2>&1 &
PID=$!
wait $PID
trap - TERM INT
wait $PID
exit $?
