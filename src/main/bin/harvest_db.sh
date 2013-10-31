#!/bin/bash

usage() {
        echo "Usage: `basename $0` environment jdbc_user jdbc_passwd [runMode]"
}

if [ $# -lt 3 ];then usage $0; exit; fi

java -cp additionalLibs/* -Denvironment=$1 -Djdbc.user=$2 -Djdbc.pw=$3 -Drun.mode=$4 -Dlog4j.configuration=prod-log4j.xml -jar json-harvester-client.jar jdbc