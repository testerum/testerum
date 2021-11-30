package appium.hooks

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import io.appium.java_client.service.local.AppiumDriverLocalService
import java.net.URL
import javax.annotation.concurrent.GuardedBy

object AppiumService {

    private val lock = Object()

    @GuardedBy("lock")
    private var service: AppiumDriverLocalService? = null

    fun globalSetup() {
        synchronized(lock) {
            service = AppiumDriverLocalService.buildDefaultService()
            service?.start()
        }
    }

    @AfterAllTests
    fun globalTearDown() {
        synchronized(lock) {
            if (service != null) {
                service?.stop()
                service = null
            }
        }
    }

    fun getServiceUrl(): URL? {
        return service?.getUrl()
    }
}
