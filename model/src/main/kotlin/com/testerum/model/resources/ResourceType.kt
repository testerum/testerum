package com.testerum.model.resources

import com.testerum.model.infrastructure.path.Path

enum class ResourceType(val relativeRootDir: String,
                        val fileExtension: String,
                        val javaType: String? = null) {

    RDBMS_CONNECTION    ("RDBMS/Connections"   , "rdbms.connection.yaml", "com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig"),
    RDBMS_SQL           ("RDBMS/SQL"           , "sql"),
    RDBMS_VERIFY        ("RDBMS/Verify"        , "rdbms.verify.json"),

    HTTP_REQUEST        ("HTTP/Request"        , "http.request.yaml"        , "com.testerum.model.resources.http.request.HttpRequest"),
    HTTP_RESPONSE_VERIFY("HTTP/Response Verify", "http.response.verify.yaml", "http.response.verify.model.HttpResponseVerify"),
    HTTP_MOCK_SERVER    ("HTTP/Mock/Server"    , "http.mock.server.yaml"    , "com.testerum.model.resources.http.mock.server.HttpMockServer"),
    HTTP_MOCK_STUB      ("HTTP/Mock/Stub"      , "http.stub.yaml"           , "com.testerum.model.resources.http.mock.stub.HttpMock"),

    JSON_VERIFY         ("JSON/Verify"         , "verify.json"),
    JSON                ("JSON/Resource"       , "json"),
    ;

    companion object {
        fun getByFileName(fileName: String): ResourceType? {
            return ResourceType.values().firstOrNull {
                fileName.endsWith(it.fileExtension)
            }
        }

        fun getByFileExtension(path: Path): ResourceType? {
            return ResourceType.values().firstOrNull {
                it.fileExtension == path.fileExtension
            }
        }

        fun getByFileExtension(fileExtension: String): ResourceType? {
            return ResourceType.values().firstOrNull {
                it.fileExtension == fileExtension
            }
        }
    }

}
