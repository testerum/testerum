package com.testerum.web_backend.services.user.security.model

data class TesterumUser(val email: String,
                        val passwordHash: String)
