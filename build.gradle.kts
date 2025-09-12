plugins {
    id("java")
}

group = "top.xinsin"
version = "1.0-SNAPSHOT"

tasks.jar {
    archiveFileName.set("XMTL-${version}.jar")

    // 配置清单文件（可选）
    manifest {
        attributes(
            "Main-Class" to "top.xinsin.Main",
            "Implementation-Title" to "XMTL to minecraft",
            "Implementation-Version" to version
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jline/jline
    implementation("org.jline:jline:3.30.5")
    // https://mvnrepository.com/artifact/org.fusesource.jansi/jansi
    implementation("org.fusesource.jansi:jansi:2.4.2")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    implementation("org.projectlombok:lombok:1.18.40")
    // https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2
    implementation("com.alibaba.fastjson2:fastjson2:2.0.58")
    // https://mvnrepository.com/artifact/me.tongfei/progressbar
    implementation("me.tongfei:progressbar:0.10.1")

    annotationProcessor("org.projectlombok:lombok:1.18.40")
    compileOnly("org.projectlombok:lombok:1.18.40")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Jar>("fatJar") {
    archiveFileName.set("XMTL-lib-${version}.jar")
    manifest {
        attributes["Main-Class"] = "top.xinsin.Main"
        attributes["Implementation-Title"] to "XMTL to minecraft"
        attributes["Implementation-Version"] to version
    }

    // 处理重复文件策略 - 解决之前的错误
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    // 包含所有依赖
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    // 排除不必要的文件
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    exclude("META-INF/maven/**")
}

tasks.test {
    useJUnitPlatform()
}
