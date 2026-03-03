plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "br.com.item"
version = "0.0.1-SNAPSHOT"
description = "Projeto de exemplo conexão com DynamoDB"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	//DynamoDB
	implementation(platform("software.amazon.awssdk:bom:2.25.30"))

	implementation("software.amazon.awssdk:dynamodb")
	implementation("software.amazon.awssdk:dynamodb-enhanced")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
