# DanMachi - MEMORIA FREESE News Crawler

Simple Java application to crawl and store past in-game news for the DanMachi - MEMORIA FREESE game.


## Features
- [ ] Recursive crawler to identify and store valid links within each news
- [ ] Crawls both EN/JP API

## Environment
This Java crawler application is written under the following environment:
- OpenJDK 11.0.9.1
- jsoup 1.12.1


## Compilation
Manually compiling .java files:
```shell
javac -cp *.jar *.java
java -cp ".:*" Crawler
```

## Licensing
This project is licensed under the MIT License - see the LICENSE.txt file for details.
