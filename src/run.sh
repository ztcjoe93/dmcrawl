#!/bin/bash

javac -cp ../lib/*.jar *.java
jar -cvfe DanmemoCrawler.jar Crawler ./
java -cp '.:../lib/*' Crawler
