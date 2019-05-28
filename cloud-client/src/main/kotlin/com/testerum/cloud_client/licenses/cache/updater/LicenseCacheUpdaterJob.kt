package com.testerum.cloud_client.licenses.cache.updater

import com.testerum.cloud_client.licenses.cache.LicensesCache
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext

@DisallowConcurrentExecution
class LicenseCacheUpdaterJob(private val licensesCache: LicensesCache) : Job {

    override fun execute(context: JobExecutionContext?) {
        licensesCache.updateFromCloud()
    }

}
