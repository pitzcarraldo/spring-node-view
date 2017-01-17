# Spring Node View Resolver

## [Node.js](https://nodejs.org) view resolver for [Spring MVC](https://spring.io) based on [J2V8](https://github.com/eclipsesource/J2V8)
[![Build Status](https://travis-ci.org/Pitzcarraldo/spring-node-view.svg?branch=master)](https://travis-ci.org/Pitzcarraldo/spring-node-view)
[![Coverage Status](https://coveralls.io/repos/Pitzcarraldo/spring-node-view/badge.png?branch=master)](https://coveralls.io/r/Pitzcarraldo/spring-node-view?branch=master)

- What is Node.js: [Node.js](https://nodejs.org)
- What is J2V8: [J2V8](https://github.com/eclipsesource/J2V8)

## Getting Started

### Gradle

```groovy
buildscript {
    repositories {
        ...
        mavenCentral()
        ...
    }
    dependencies {
        ...
        classpath 'com.google.gradle:osdetector-gradle-plugin:1.4.0'
        classpath "com.layer:gradle-git-repo-plugin:2.0.2"
        ...
    }
}

apply plugin: 'com.google.osdetector'
apply plugin: 'git-repo'

repositories {
    mavenCentral()
    github("Pitzcarraldo", "maven", "master", "snapshots")
}

dependencies {
    ...
    compile("com.github.pitzcarraldo:spring-node-view:0.1.${osdetector.os}-SNAPSHOT")
    ....
}

```


### Maven

```xml
    <dependencies>
        ...
        <dependency>
            <groupId>com.github.pitzcarraldo</groupId>
            <artifactId>spring-node-view</artifactId>
            <version>0.1.${inster_your_os, [osx|linux|windows]}-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>
    
    <repositories>
    	...
  	<repository>
                <id>com.github.pitzcarraldo</id>
                <url>https://github.com/Pitzcarraldo/maven/raw/master/snapshots</url>
    </repository>
  	...
    </repositories>
```

### Spring configuration

**JavaConfig**

```java
  @Bean
  public ViewResolver getViewResolver {
    ViewResolver resolver = new NodeViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".js");
    resolver.setViewClass(classOf[NodeView]);
    resolver.setContentType("text/html;charset=UTF-8");
    resolver.setCache(false);
    // resolver.setCache(true);
    // resolver.setCacheLimit(100); // default 1024
    // resolver.setCacheExpireTime(1); // minutes, default 5 
    return resolver;
  }
```

**ScalaConfig**

```scala
  @Bean
  def getViewResolver: ViewResolver = {
    val resolver = new NodeViewResolver
    resolver.setPrefix("/WEB-INF/views/")
    resolver.setSuffix(".js")
    resolver.setViewClass(classOf[NodeView])
    resolver.setContentType("text/html;charset=UTF-8")
    resolver.setCache(false)
    resolver
  }
```

### Template Format

**Sync Template**

```js
// Just return generated HTML as String
module.exports = function(model) {
    // ...
    return '<html><head>~~~</head><body>~~~</body></html>';
}

// Also could respond headers and body as Object
module.exports = function(model) {
    // ...
    return {
        headers: {
            'Content-Type': 'application/json'
        },
        body: {
            "key": "value"
        }
    };
}

// Set Cookie with HTML
module.exports = function(model) {
  // ...
  return {
      headers: {
        'Set-Cookie': 'userId=Pitzcarraldo; Domain=github.com; Expires=Wed, 17-Jan-2018 18:36:54 GMT; '
      },
      body: '<html><head>~~~</head><body>~~~</body></html>'
  };
}
```

**Async Template**

```js
// Just return callback with generated HTML as String
module.exports = function(model, callback) {
    // ...
    callback('<html><head>~~~</head><body>~~~</body></html>');
}

// Also could respond headers and body as Object
module.exports = function(model, callback) {
    // ...
    callback({
        headers: {
            'Content-Type': 'application/json'
        },
        body: {
            "key": "value"
        }
    });
}

// Set Cookie with HTML
module.exports = function(model, callback) {
  // ...
  callback({
      headers: {
        'Set-Cookie': 'userId=Pitzcarraldo; Domain=github.com; Expires=Wed, 17-Jan-2018 18:36:54 GMT; '
      },
      body: '<html><head>~~~</head><body>~~~</body></html>'
  });
}
```