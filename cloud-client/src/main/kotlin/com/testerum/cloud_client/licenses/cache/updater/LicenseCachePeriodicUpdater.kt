package com.testerum.cloud_client.licenses.cache.updater

import com.testerum.cloud_client.licenses.cache.LicensesCache
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

class LicenseCachePeriodicUpdater(private val licensesCache: LicensesCache,
                                  private val cronExpression: String) {

    private var initialized: Boolean = false
    private val initializedLock = Object()

    private val quartzScheduler: Scheduler = StdSchedulerFactory().scheduler

    fun initialize() {
        synchronized(initializedLock) {
            if (initialized) {
                return
            }

            initialized = true
        }

        this.quartzScheduler.setJobFactory(
                LicenseCacheUpdaterJobFactory(licensesCache)
        )

        val immediateJobDetail: JobDetail = JobBuilder.newJob(LicenseCacheUpdaterJob::class.java)
                .withIdentity("licenseCacheValidatorImmediateJob")
                .build()

        val immediateTrigger = TriggerBuilder.newTrigger()
                .withIdentity("licenseCacheValidatorImmediateTrigger")
                .startNow()
                .build()

        val cronJobDetail: JobDetail = JobBuilder.newJob(LicenseCacheUpdaterJob::class.java)
                .withIdentity("licenseCacheValidatorCronJob")
                .build()

        val cronTrigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("licenseCacheValidatorCronTrigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(cronExpression)
                                .withMisfireHandlingInstructionDoNothing()
                )
                .build()

        this.quartzScheduler.scheduleJob(immediateJobDetail, immediateTrigger)
        this.quartzScheduler.scheduleJob(cronJobDetail, cronTrigger)

        this.quartzScheduler.start()
    }

    fun shutdown() {
        this.quartzScheduler.shutdown(false)
    }

}
