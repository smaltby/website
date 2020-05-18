import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.2.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	war
	kotlin("jvm") version "1.3.71"
	kotlin("plugin.spring") version "1.3.71"
}

group = "com.seanmaltby"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	// Note, most versions are provided by spring dependency management plugin
	// Supported dependencies and associated versions: https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-dependency-versions.html

	// templating engine
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")

	// web framework
	implementation("org.springframework.boot:spring-boot-starter-web")

	// security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// object-relational mapping (orm), for interacting with database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// database
	implementation("org.postgresql:postgresql")

	// database migrations
	implementation("org.flywaydb:flyway-core")

	// IO utility classes, mainly used for managing dynamic resources in the file system.
	// Version required, not supplied by spring dependency management
	implementation("commons-io:commons-io:2.6")

	// required to be in the class path for kotlin support
	// https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-kotlin.html#boot-features-kotlin-requirements
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// testing utilities for spring boot
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	// dev tools, compile time only as it isn't required at runtime, used for automatic restarts on classpath changes
	compileOnly("org.springframework.boot:spring-boot-devtools")

	// servlet api, part of the application server, so needed at compile time only to reference the api
	compileOnly("javax.servlet:javax.servlet-api")
}

tasks.withType<BootJar>().configureEach {
	launchScript()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
