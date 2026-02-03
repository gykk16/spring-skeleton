# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Skills

> Before starting any task, **check if there is an appropriate skill available**. Use the Skill tool to invoke skills for
common tasks.

## Language

- **Default**: Respond in professional, understandable, easy English
- **Korean**: If user asks to respond in Korean, use 존댓말 (formal/polite speech) as default

## Project Rules

All coding standards and guidelines are maintained in the `.claude/rules/` directory:

- `01_cleancode.md` - Clean code principles (KISS, DRY, readable code)
- `09_test.md` - Test generation rules (AssertJ, given-when-then, meaningful tests)
- `10_kotlin.md` - Kotlin best practices (nullability, immutability, idioms)
- `15_sql.md` - SQL style guide (formatting, naming, no FK/index by default)
- `90_project-convention.md` - Project conventions (ApiResource response format)
- `91_project-modules.md` - Project modules & architecture (hexagonal architecture)

Claude Code automatically reads rules from this directory.

## Build Commands

```bash
# Build
./gradlew build

# Test
./gradlew test

# Test specific class
./gradlew test --tests "io.glory.common.utils.SomeTest"
```

## Documentation

Project documentation is maintained in the `.docs/` directory:

- `.docs/versioning-and-release.adoc` - Versioning and release guide
- `.docs/architecture/` - Architecture documentation
  - `00-overview.adoc` - Project overview
  - `01-module-dependency.adoc` - Module dependency structure
  - `02-request-lifecycle.adoc` - Request lifecycle
  - `03-error-handling.adoc` - Error handling strategy
  - `04-caching-strategy.adoc` - Caching strategy
  - `05-http-client-patterns.adoc` - HTTP client patterns
  - `06-cross-cutting-concerns.adoc` - Cross-cutting concerns (AOP, filters)
  - `07-api-response-format.adoc` - API response format and ResponseCode
  - `08-new-module-guide.adoc` - Guide for creating new modules

## Project Structure

- `modules/common` - Common utilities and shared code
