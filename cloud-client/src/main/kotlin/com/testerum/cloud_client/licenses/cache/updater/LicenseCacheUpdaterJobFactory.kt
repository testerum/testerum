package com.testerum.cloud_client.licenses.cache.updater

import com.testerum.cloud_client.licenses.cache.LicensesCache
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle

class LicenseCacheUpdaterJobFactory(private val licensesCache: LicensesCache) : JobFactory {

    override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        return LicenseCacheUpdaterJob(licensesCache)
    }

}
