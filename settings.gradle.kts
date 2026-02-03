rootProject.name = "spring-skeleton"

include(
    // Foundation
    "modules:common",
    "modules:common-web",
    "modules:test-support",

    // Infrastructure
    "modules:infrastructure",

    // Core Business
//    "modules:domain",
    "modules:application:todo-application",

    // Runtime
    "modules:bootstrap:skeleton-api-app",
    "modules:bootstrap:skeleton-worker-app",
)
