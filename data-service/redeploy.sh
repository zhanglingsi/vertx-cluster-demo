#!/usr/bin/env bash

mvn clean package
java -jar target/data-service-fat.jar -cluster -conf conf/local.json

