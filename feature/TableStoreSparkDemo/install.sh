#!/usr/bin/env sh
# shellcheck disable=SC2046
# shellcheck disable=SC2006
cd `dirname "$0"` || exit

# Install tablestore spark connector preview version
mvn install:install-file -Dfile=libs/emr-tablestore-2.2.0-SNAPSHOT.jar -DartifactId=emr-tablestore -DgroupId=com.aliyun.emr -Dversion=2.2.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

