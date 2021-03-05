#!/usr/bin/env bash

cd spring-datasource-boot-autoconfigure && mvn clean deploy -DskipTests=true -P snapshot
cd ../spring-datasource-boot-starter && mvn clean deploy -DskipTests=true -P snapshot
cd ../spring-memcache-boot-autoconfigure && mvn clean deploy -DskipTests=true -P snapshot
cd ../spring-memcache-boot-starter && mvn clean deploy -DskipTests=true -P snapshot
cd ../spring-multi-datasource-boot-autoconfigure && mvn clean deploy -DskipTests=true -P snapshot
cd ../spring-multi-datasource-boot-starter && mvn clean deploy -DskipTests=true -P snapshot