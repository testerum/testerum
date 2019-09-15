package com.testerum.model.resources

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.model.text.parts.param_meta.StringTypeMeta
import com.testerum.model.text.parts.param_meta.TypeMeta

enum class ResourceType(val relativeRootDir: String,
                        val fileExtension: String,
                        val typeMeta: TypeMeta) {

    RDBMS_CONNECTION    ("RDBMS/Connections"   , "rdbms.connection.yaml"    , ObjectTypeMeta("com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig")),
    RDBMS_SQL           ("RDBMS/SQL"           , "sql"                      , StringTypeMeta()),
    RDBMS_VERIFY        ("RDBMS/Verify"        , "rdbms.verify.json"        , StringTypeMeta()),

    HTTP_REQUEST        ("HTTP/Request"        , "http.request.yaml"        , ObjectTypeMeta("com.testerum.model.resources.http.request.HttpRequest")),
    HTTP_RESPONSE_VERIFY("HTTP/Response Verify", "http.response.verify.yaml", ObjectTypeMeta("http.response.verify.model.HttpResponseVerify")),
    HTTP_MOCK_SERVER    ("HTTP/Mock/Server"    , "http.mock.server.yaml"    , ObjectTypeMeta("com.testerum.model.resources.http.mock.server.HttpMockServer")),
    HTTP_MOCK_STUB      ("HTTP/Mock/Stub"      , "http.stub.yaml"           , ObjectTypeMeta("com.testerum.model.resources.http.mock.stub.HttpMock")),

    JSON_VERIFY         ("JSON/Verify"         , "verify.json"              , StringTypeMeta()),
    JSON                ("JSON/Resource"       , "json"                     , StringTypeMeta()),
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
