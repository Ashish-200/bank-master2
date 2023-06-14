plugins {
    id("java")
    id("org.springframework.boot") version "2.7.2"
    id ("io.spring.dependency-management") version "1.0.13.RELEASE"
    application
}

group = "ltr.org"
version = "Dev_1.0"

repositories {
    mavenCentral()
    mavenLocal()
}
application {
    applicationDefaultJvmArgs = listOf(
        "-Dspring.configuration.uri=http://localhost:8083/configService/get/Dev/bankMaster",
        "-Dspring.profiles.active=dev",
        "-Dspring.help.url=http://localhost:8083/configService/get/Help/bankMaster"
    )
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.2")
    implementation("org.springframework.boot:spring-boot-starter-security:2.7.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:2.7.2")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa:2.7.2")
    implementation("org.postgresql:postgresql:42.5.0")
    implementation("org.springframework.data:spring-data-envers:2.7.2")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis:2.7.2")
    implementation ("org.springframework.boot:spring-boot-starter-actuator:2.7.2")
    implementation("io.springfox:springfox-swagger-ui:3.0.0")
    implementation("io.springfox:springfox-swagger2:3.0.0")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.apache.commons:commons-lang3:3.10")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("org.modelmapper:modelmapper:3.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.2")
    implementation(files("libs/common-config-dev.jar"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}