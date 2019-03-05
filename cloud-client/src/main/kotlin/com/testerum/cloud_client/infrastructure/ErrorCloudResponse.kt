package com.testerum.cloud_client.infrastructure

data class ErrorCloudResponse(val error: CloudError)

data class CloudError(val code: Int,
                      val message: String)
