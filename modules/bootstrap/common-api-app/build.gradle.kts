// Common API Application module

dependencies {
    val developmentOnly by configurations

    implementation(project(":modules:application:common-application"))

    // Development
    developmentOnly(rootProject.libs.spring.boot.docker.compose)

    // Test
    testImplementation(testFixtures(project(":modules:test-support")))
}

tasks.processResources {
    filesMatching("**/application*.yml") {
        filter<org.apache.tools.ant.filters.ReplaceTokens>(
            "tokens" to mapOf("projectVersion" to project.version.toString())
        )
    }
}
