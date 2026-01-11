// Bootstrap Application module

dependencies {
    val developmentOnly by configurations
    
    implementation(project(":modules:application:todo-application"))

    // Development
    developmentOnly(rootProject.libs.spring.boot.docker.compose)
}

tasks.processResources {
    filesMatching("**/application*.yml") {
        filter<org.apache.tools.ant.filters.ReplaceTokens>(
            "tokens" to mapOf("projectVersion" to project.version.toString())
        )
    }
}
