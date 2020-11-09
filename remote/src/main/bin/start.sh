#!/bin/bash
export PIPE_HOME=$(cd $(dirname $0)/..; pwd)
export CLASSPATH=${PIPE_HOME}/conf:${PIPE_HOME}/lib/*:${CLASSPATH}
if [ -z ${JAVA_HOME} ]; then
  command=java
else
  command=${JAVA_HOME}/bin/java
fi
PIPE_OPTS="-Xmx1G -Xms1G -Dlog.dir=${PIPE_HOME}/logs ${PIPE_OPTS}"
nohup $command ${PIPE_OPTS} com.lcaaaat.pipe.remote.PipeRemote > ${PIPE_HOME}/nohup.out 2>&1 &
echo $! > ${PIPE_HOME}/pidfile