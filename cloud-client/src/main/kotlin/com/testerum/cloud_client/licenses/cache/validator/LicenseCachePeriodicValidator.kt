package com.testerum.cloud_client.licenses.cache.validator

import com.testerum.cloud_client.licenses.cache.LicensesCache
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

class LicenseCachePeriodicValidator(private val licensesCache: LicensesCache,
                                    private val cronExpression: String) {

    private val quartzScheduler: Scheduler = StdSchedulerFactory().scheduler

    fun initialize() {
        this.quartzScheduler.setJobFactory(
                LicenseCacheValidatorJobFactory(licensesCache)
        )

        val jobDetail: JobDetail = JobBuilder.newJob(LicenseCacheValidatorJob::class.java)
                .withIdentity("licenseCacheValidatorJob")
                .build()

        val trigger: Trigger = TriggerBuilder.newTrigger()
                .withIdentity("licenseCacheValidatorTrigger")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(cronExpression)
                                .withMisfireHandlingInstructionDoNothing()
                )
                .build()

        this.quartzScheduler.scheduleJob(jobDetail, trigger)

        this.quartzScheduler.start()
    }

    fun shutdown() {
        this.quartzScheduler.shutdown(false)
    }

}
