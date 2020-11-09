#!/bin/bash
export PIPE_HOME=$(cd $(dirname $0)/..; pwd)
PID=$(cat ${PIPE_HOME}/pidfile)
kill ${PID}