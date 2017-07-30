# Issues

1. act-ebean 을 사용할 경우 ebean -> avaje-datasource 모듈에서 slf4j-api 를 [1.7.12,) 이런 식을 참조하여
결국 최신 모듈을 받게 되는데 이 slf4j-api 1.8.0-alpha2 모듈이 jdk9 을 사용하여 오류 발생. build.gradle 에서 slf4j-api 의 버전을 강제함.