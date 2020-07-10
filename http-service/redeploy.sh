#!/usr/bin/env bash


mvn clean package
java -jar target/http-service-fat.jar -cluster -conf conf/local.json

