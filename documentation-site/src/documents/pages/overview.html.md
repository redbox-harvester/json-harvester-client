```
title: Overview
layout: page
tags: ['intro','page']
pageOrder: 1
```

The Harvester Client is a generic tool for transferring data between systems. The tool can connect to a variety of data sources. These include but is not limited to files, databases(with JDBC drivers) and CSV files. By default, it transforms data into a JSON document. Optionally, it can be configured to execute scripts to perform custom processing of data. The resulting output is then sent to the target system, through a variety of ways, including but not limited to files, database (JDBC support) and JMS messages. The tool is built with [Groovy][groovy] and [Spring Integration][SI-Main].

The Harvester Client implementation can be illustrated as such:

![Harvester Client implementation diagram][img-HC_Core]

## Components
### Source
The data source. Out of the box, the Harvester Client can connect to any data source that is supported by [Spring Integration inbound adapters][SI-Adapter].

### Target
The recipient of the data. Out of the box, the Harvester Client can push data to any system that is supported by [Spring Integration outbound adapters][SI-Adapter].

### Utilities
A library of handy classes that can be reused across many Harvester Client or Harvester Client-related projects.

### Harvester Client
This project, the core implementation.

### Spring Integration Harvester configuration
The config files that set up the Spring Integration channels. This typically comprise of the Spring Integration Bean definitions and a Config file.

### Scripts
Optional JVM-supported scripts that are executed during the data flow. These scripts have full access to the data, it can alter or drop the data as required.


[groovy]: http://groovy.codehaus.org/ "Groovy Project Site"
[SI-Main]: http://projects.spring.io/spring-integration/ "Spring Integration Project Site"
[SI-Adapter]: http://docs.spring.io/spring-integration/reference/htmlsingle/#overview-endpoints-channeladapter "Spring Integration Adapters"
[img-HC_Core]: /images/HarvesterClient-Implementation-HighLevel.png "Harvester Client Implementation Diagram"
