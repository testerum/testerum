/**
 * NOTE: if this script returns false, the build log with more information can be found at target/it/simple/build.log
 */

import java.util.jar.*

def jarFile = new JarFile(new File(basedir, "target/test-project-1.0.jar"))
try {
    def entry = jarFile.getEntry("META-INF/testerum/extensions-info.tfsf")

    if (entry == null) {
        println "file not found in jar"
        return false
    } else {
        if (jarFile.getInputStream(entry).getBytes().length == 0) {
            println "empty file"
            return false
        }
    }
} finally {
    jarFile.close()
}

return true
