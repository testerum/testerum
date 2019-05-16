package com.testerum.cloud_client.licenses.cache.validator

import com.testerum.cloud_client.licenses.cache.LicensesCache
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext

@DisallowConcurrentExecution
class LicenseCacheValidatorJob(private val licensesCache: LicensesCache) : Job {

    override fun execute(context: JobExecutionContext?) {
        licensesCache.validate()
    }

}
