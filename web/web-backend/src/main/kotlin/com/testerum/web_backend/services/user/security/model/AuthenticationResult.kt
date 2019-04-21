package com.testerum.web_backend.services.user.security.model

sealed class AuthenticationResult

object UnauthenticatedAuthenticationResult : AuthenticationResult()

data class AuthenticatedAuthenticationResult(val user: TesterumUser) : AuthenticationResult()
