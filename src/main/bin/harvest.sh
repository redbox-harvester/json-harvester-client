#!/bin/bash

usage() {
        echo "Usage: `basename $0` clientType environment [runMode]"
}

if [ $# -lt 2 ];then usage $0; exit; fi

java -cp additionalLibs/* -Denvironment=$2 -Drun.mode=$3 -Dlog4j.configuration=prod-log4j.xml -jar json-harvester-client.jar $1