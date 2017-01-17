# Spring Node View Resolver

## [Node.js](https://nodejs.org) view resolver for [Spring MVC](https://spring.io) based on [J2V8](https://github.com/eclipsesource/J2V8)

[![Build Status](https://travis-ci.org/Pitzcarraldo/spring-node-view.svg?branch=master)](https://travis-ci.org/Pitzcarraldo/spring-node-view)
[![Coverage Status](https://coveralls.io/repos/github/Pitzcarraldo/spring-node-view/badge.svg?branch=master)](https://coveralls.io/github/Pitzcarraldo/spring-node-view?branch=master)

## Use any type of JavaScript View Framework(React.js, Vue.js, etc) you want with Spring Project in Server Side!
### ⚠️️ Caution: This library is experimental yet. Please be careful when using for production service.

- This Library is fully written in [Scala](https://www.scala-lang.org/), but works well with Java too.
- Support Spring MVC 3 and 4

## Getting Started

### Gradle

```groovy
buildscript {
    // ...
    dependencies {
        // ...
        classpath 'com.google.gradle:osdetector-gradle-plugin:1.4.0'
    }
}

apply plugin: 'com.google.osdetector'

repositories {
    // ...
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

dependencies {
    // ...
    compile("com.github.pitzcarraldo:spring-node-view:0.1.0.${osdetector.os}-SNAPSHOT")
}

```


### Maven

```xml
    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.4.0.Final</version>
            </extension>
        </extensions>
    </build>
    <repositories>
        <repository>
            <id>sonatype-snapshots</id>
             <url>https://oss.sonatype.org/content/repositories/snapshots</url>
             <releases><enabled>false</enabled></releases>
             <snapshots><enabled>true</enabled></snapshots>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.pitzcarraldo</groupId>
            <artifactId>spring-node-view</artifactId>
            <version>0.1.0.${os.detected.name}-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

### Spring configuration

**JavaConfig**

```java
  @Bean
  public ViewResolver nodeViewResolver() {
    NodeViewResolver resolver = new NodeViewResolver();
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".js");
    resolver.setViewClass(NodeView.class);
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
  def nodeViewResolver: ViewResolver = {
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
    return '<html><head>...</head><body>...</body></html>';
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
      body: '<html><head>...</head><body>...</body></html>'
  };
}
```

**Async Template**

```js
// Just return callback with generated HTML as String
module.exports = function(model, callback) {
    // ...
    callback('<html><head>...</head><body>...</body></html>');
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
      body: '<html><head>...</head><body>...</body></html>'
  });
}
```

## Trouble Shooting

This library uses the latest version of dependencies. And they can conflict with your project. You can exclude conflicting dependencies as follows:

```groovy
  compile("com.github.pitzcarraldo:spring-node-view:0.1.0.${osdetector.os}-SNAPSHOT") {
    // Please uncomment the dependency that you want to exclude.
    // exclude group: 'com.google.guava', module: 'guava'
    // exclude group: 'com.google.code.findbugs', module: 'jsr305'
    // exclude group: 'javax.servlet', module: 'javax.servlet-api'
    // exclude group: 'com.fasterxml.jackson.core', module: 'jackson-databind'
    // exclude group: 'org.springframework', module: 'spring-webmvc'
  }
```

## TODO

- Documentation
- Performance Test and Tuning
- Improve Test Coverage

## LICENSE

[MIT](LICENSE)
