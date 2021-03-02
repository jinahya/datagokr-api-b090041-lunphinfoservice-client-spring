# b090041-lunphdinfoservice-client-spring

[![Java CI with Maven](https://github.com/jinahya/datagokr-api-b090041-lunphdinfoservice-client-spring/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/jinahya/datagokr-api-b090041-lunphdinfoservice-client-spring/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_datagokr-api-b090041-lunphdinfoservice-client-spring&metric=alert_status)](https://sonarcloud.io/dashboard?id=jinahya_datagokr-api-b090041-lunphdinfoservice-client-spring)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.jinahya/datagokr-api-b090041-lunphdinfoservice-client-spring)](https://search.maven.org/search?q=a:datagokr-api-b090041-lunphdinfoservice-client-spring)
[![javadoc](https://javadoc.io/badge2/com.github.jinahya/datagokr-api-b090041-lunphdinfoservice-client-spring/javadoc.svg)](https://javadoc.io/doc/com.github.jinahya/datagokr-api-b090041-lunphdinfoservice-client-spring)

A client library for accessing http://apis.data.go.kr/B090041/openapi/service/LunPhInfoService.

See [월령정보(data.go.kr)](https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15012689) and/or [월별천문현상(천문연구원)](http://astro.kasi.re.kr/life/pageView/7).

## Verify

Verify with your own service key assigned by the service provider.

```shell
$ mvn -Pfailsafe -DservcieKey=... clean verify
```

You may put your service key on `src/test/resources/failsafe.system.properties`, which is `.gitignore`d, like this,
```
serviceKey=...
```
and verify like this.
```shell
$ mvn -Pfailsafe clean verify
```

## Injection points

### Common

|Qualifier|Type|Notes|
|---------|----|-----------|
|`@LunPhInfoServiceServiceKey`|`java.lang.String`|Provided by the service provider|

### For `LunPhInfoServiceClient` with `RestTemplate`

|Qualifier|Type|Notes|
|---------|----|-----|
|`@LunPhInfoServiceRestTemplate`|[`RestTemplate`][RestTemplate]||
|`@LunPhInfoServiceRestTemplateRootUri`|`String`|Optional|

### For `LunPhInfoServiceReactiveClient` with `WebClient`

|Qualifier|Type|Notes|
|---------|----|-----|
|`@LunPhInfoServiceWebClient`|[`WebClient`][WebClient]||

## Usages

Expand the component-scanning path.

```java

@SpringBootApplication(
        scanBasePackageClasses = {
                com.github.jinahya.datagokr.....client.NoOp.class,
                MyApplication.class
        }
)
class MyApplication {

}
```

Provide the service key assigned by the service provider. Note that the service provider may give you a URL-encoded value. You should use a URL-decoded value.

```java
@AbstractLunPhInfoServiceClient.LunPhInfoServiceServiceKey
@Bean
public String lunPhInfoServiceServiceKey(){
    // The service key assigned by data.go.kr
    // Might be already URL-encoded
    // Use a URL-decoded value    
    // return "...%3D%3D"; (X)
    // return "...==";     (O)
}
```

### Using `LunPhInfoServiceClient` with `RestTemplate`

Provide an instance of `RestTemplate`.

```java
@LunPhInfoServiceRestTemplate
@Bean
public RestTemplate lunPhInfoServiceRestTemplate() {
    return new RestTemplateBuilder()
            ...
            .rootUri(AbstractLunPhInfoServiceClient.BASE_URL_PRODUCTION)
            .build();
}
```

Get `@Autowired` with an instance of `LunPhInfoServiceClient` which is internally got autowired with the `RestTemplate` instance.

```java
@Autowired
private LunPhInfoServiceClient client;
```

### Using `LunPhInfoServiceReactiveClient` with `WebClient`

Provide an instance of `WebClient`.

```java
@LunPhInfoServiceWebClient
@Bean
public WebClient lunPhInfoServiceWebClient() {
    return WebClient.builder()
            ...
            .baseUrl(AbstractLunPhInfoServiceClient.BASE_URL_PRODUCTION)
            .build();
}
```

Get `@Autowired` with an instance of `LunPhInfoServiceReactiveClient` which is internally got autowired with the `WebClient` instance.

```java
@Autowired
private LunPhInfoServiceReactiveClient client;
```

[RestTemplate]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
[WebClient]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html