package io.glory.commonweb.security.jwt

import io.glory.commonweb.security.Role

data class JwtPayload(
    val issuer: String,
    val subject: String,
    val role: Role,
)
