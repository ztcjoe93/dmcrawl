# DanMachi - MEMORIA FREESE News Crawler

Simple Java application to crawl and store past in-game news for the DanMachi - MEMORIA FREESE game.


## Features
- [x] Recursive crawler to identify and store valid links within each category of news (News, DanMachi Info, Update, Malfunction)
- [x] Local storage for images/css/js instead of referencing to the CDN
- [x] Crawls both EN/JP API

## In-progress
- [ ] GUI for users
- [ ] News selection UI for user 

## Environment
This Java crawler application is written under the following environment:
- OpenJDK 11.0.9.1
- jsoup 1.12.1


## Compilation
Manually compiling .java files:
```shell
javac -cp ../lib/*.jar *.java
java -cp ".:../lib/*" Crawler
```

Or if you're on Linux, simply run the shell script in the src directory with:
```shell
./run.sh
```

## Licensing
This project is licensed under the MIT License - see the LICENSE.txt file for details.
