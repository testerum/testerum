package net.qutester.model.repository.enums

import net.qutester.model.infrastructure.path.Path

enum class FileType constructor(val relativeRootDirectory: Path, val fileExtension: String) {

    MANUAL_TEST(Path.createInstance("manual_tests/tests"), "manual_test"),
    MANUAL_TESTS_RUNNER(Path.createInstance("manual_tests/tests_runner"), "manual_tests_runner"),

    RDBMS_CONNECTION(Path.createInstance("resources/RDBMS/Connections"), "rdbms.connection.json"),
    RDBMS_SQL(Path.createInstance("resources/RDBMS/SQL"),"sql"),
    RDBMS_VERIFY(Path.createInstance("resources/RDBMS/Verify"),"rdbms.verify.json"),
    HTTP_REQUEST(Path.createInstance("resources/HTTP/Request"),"http.request.json"),
    HTTP_RESPONSE_VERIFY(Path.createInstance("resources/HTTP/Response"),"http.response.verify.json"),
    HTTP_MOCK_SERVER(Path.createInstance("resources/HTTP/Mock/Server"),"http.mock.server.json"),
    HTTP_MOCK_STUB(Path.createInstance("resources/HTTP/Mock/Stub"),"http.stub.json"),
    JSON_VERIFY(Path.createInstance("resources/JSON/Verify"),"verify.json"),

    FEATURE(Path.createInstance("features"), "feat"),
    TEST(Path.createInstance(   "features"), "test"),

    STEP(Path.createInstance(     "composed_steps"), "step"),
    RESULT(Path.createInstance(   "results"),        "result"),
    RESOURCE(Path.createInstance( "resources"),      "not_known"),
    VARIABLES(Path.createInstance("variables"),      "json"),
    ;

    fun resolveToRootRepository(path: Path): Path {
        return this.relativeRootDirectory.resolve(path)
    }
    
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

        @JvmStatic
        fun getByRelativePath(relativePath: String): FileType? {
            for (fileType in values()) {
                if (relativePath.startsWith(fileType.relativeRootDirectory.toString()) &&
                        relativePath.endsWith(fileType.fileExtension)) {
                    return fileType;
                }
            }
            return null;
        }

    }
}