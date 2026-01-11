// Infrastructure module - Database, Cache, External services

apply(plugin = "org.springframework.boot")
apply(plugin = "org.jetbrains.kotlin.kapt")

tasks.processResources {
    filesMatching("**/redisson-*.yml") {
        expand("projectName" to rootProject.name)
    }
}

dependencies {
    val annotationProcessor by configurations
    val runtimeOnly by configurations

    // Spring Data & QueryDSL
    implementation(rootProject.libs.bundles.spring.data)

    // Cache (Caffeine, Redisson, LZ4)
    implementation(rootProject.libs.bundles.cache)

    // RestClient (HTTP Client with tracing support)
    implementation(rootProject.libs.spring.boot.starter.restclient)
    implementation(rootProject.libs.spring.boot.starter.aspectj)
    implementation(rootProject.libs.bundles.metrics)

    // Database
    runtimeOnly(rootProject.libs.h2)

    // QueryDSL Annotation Processor
    annotationProcessor(rootProject.libs.querydsl.apt.get().toString() + ":jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
}

// QueryDSL Q-class generation path
val querydslDir: Provider<Directory> = layout.buildDirectory.dir("generated/source/kapt/main")

configure<JavaPluginExtension> {
    sourceSets {
        getByName("main") {
            java.srcDir(querydslDir)
        }
    }
}
