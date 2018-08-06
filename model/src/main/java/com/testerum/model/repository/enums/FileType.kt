package com.testerum.model.repository.enums

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path

enum class FileType constructor(val relativeRootDirectory: Path,
                                val fileExtension: String,
                                val resourceJavaType: String? = null) {

    MANUAL_TEST(Path.createInstance("manual_tests/tests"), "manual_test"),
    MANUAL_TESTS_RUNNER(Path.createInstance("manual_tests/tests_runner"), "manual_tests_runner"),

    RDBMS_CONNECTION(Path.createInstance("resources/RDBMS/Connections"), "rdbms.connection.yaml", "com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig"),
    RDBMS_SQL(Path.createInstance("resources/RDBMS/SQL"),"sql"),
    RDBMS_VERIFY(Path.createInstance("resources/RDBMS/Verify"),"rdbms.verify.json"),
    HTTP_REQUEST(Path.createInstance("resources/HTTP/Request"),"http.request.yaml", "com.testerum.model.resources.http.request.HttpRequest"),
    HTTP_RESPONSE_VERIFY(Path.createInstance("resources/HTTP/Response Verify"),"http.response.verify.yaml", "http.response.verify.model.HttpResponseVerify"),
    HTTP_MOCK_SERVER(Path.createInstance("resources/HTTP/Mock/Server"),"http.mock.server.yaml", "com.testerum.model.resources.http.mock.server.HttpMockServer"),
    HTTP_MOCK_STUB(Path.createInstance("resources/HTTP/Mock/Stub"),"http.stub.yaml", "com.testerum.model.resources.http.mock.stub.HttpMock"),
    JSON_VERIFY(Path.createInstance("resources/JSON/Verify"),"verify.json"),

    FEATURE(Path.createInstance("features"), Feature.FILE_EXTENSION),
    TEST(Path.createInstance(   "features"), "test"),

    COMPOSED_STEP(Path.createInstance(     "composed_steps"), "step"),
    RESULT(Path.createInstance(   "results"),        "result"),
    RESOURCE(Path.createInstance( "resources"),      "not_known"),
    VARIABLES(Path.createInstance("variables"),      "json"),
    ;

    companion object {
        @JvmStatic
        fun getByFileName(fileName: String): FileType? {
            return values().firstOrNull {
                fileName.endsWith(it.fileExtension)
            }
        }

        @JvmStatic
        fun getByFileExtension(fileExtension: String): FileType? {
            return values().firstOrNull {
                it.fileExtension == fileExtension
            }
        }

        @JvmStatic
        fun getResourceTypeByExtension(path: Path): FileType? {
            for (value in values()) {
                if (path.fileExtension == value.fileExtension) {
                    return value
                }
            }
            return null
        }

    }
}