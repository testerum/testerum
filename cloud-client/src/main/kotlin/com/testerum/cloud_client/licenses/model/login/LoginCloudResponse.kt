package com.testerum.cloud_client.licenses.model.login

sealed class LoginCloudResponse

data class FoundLoginCloudResponse(val signedLicensedUserProfile: String) : LoginCloudResponse()

object NotFoundLoginCloudResponse : LoginCloudResponse()
