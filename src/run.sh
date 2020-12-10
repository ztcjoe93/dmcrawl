#!/bin/bash

javac -cp ../lib/*.jar *.java
java -cp '.:../lib/*' Crawler
