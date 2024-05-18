
group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation(project(":domain"))
    runtimeOnly(project(":infra"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    // mockito-kotlin and byte-buddy dependency issue
    testImplementation("net.bytebuddy:byte-buddy:1.14.15")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
