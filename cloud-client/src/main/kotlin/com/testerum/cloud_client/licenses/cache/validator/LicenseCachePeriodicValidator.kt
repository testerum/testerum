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

        val immediateJobDetail: JobDetail = JobBuilder.newJob(LicenseCacheValidatorJob::class.java)
                .withIdentity("licenseCacheValidatorImmediateJob")
                .build()

        val immediateTrigger = TriggerBuilder.newTrigger()
                .withIdentity("licenseCacheValidatorImmediateTrigger")
                .startNow()
                .build()

        val cronJobDetail: JobDetail = JobBuilder.newJob(LicenseCacheValidatorJob::class.java)
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
