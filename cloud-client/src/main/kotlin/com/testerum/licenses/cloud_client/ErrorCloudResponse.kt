package com.testerum.licenses.cloud_client

data class ErrorCloudResponse(val error: CloudError)

data class CloudError(val code: Int,
                      val message: String)
