plugins {
    id("java")
}

group = "me.iwareq"
version = "1.1.7"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    implementation("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

tasks.withType<ProcessResources> {
    filteringCharset = Charsets.UTF_8.name()
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
