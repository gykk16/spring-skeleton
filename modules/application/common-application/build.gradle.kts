// Common Application module - Common business services

dependencies {
    api(project(":modules:domain"))
    api(project(":modules:infrastructure"))
    implementation(rootProject.libs.spring.boot.starter)
    implementation(rootProject.libs.spring.boot.starter.data.jpa)
}
