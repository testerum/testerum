package com.testerum.file_service.business.trial

import com.testerum.file_service.business.trial.TrialService.Companion.EXPIRATION_PERIOD
import com.testerum.file_service.business.trial.TrialService.Companion.TRIAL_PERIOD
import com.testerum.file_service.file.trial.TrialFileService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit.DAYS
import org.hamcrest.Matchers.`is` as Is

class TrialServiceTest {

    @Test
    fun `trial period not yet started - the trial period will begin`() {
        val startDate: LocalDate? = null
        val now: LocalDate = LocalDate.of(2019, 6, 1)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(TRIAL_PERIOD.days)))
        assertThat(trialInfo.startDate, Is(equalTo(now)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(now)))
    }

    @Test
    fun `now before trial started - in expired period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.minusDays(1)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(-1)))
        assertThat(trialInfo.startDate, Is(equalTo(startDate.minus(EXPIRATION_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(EXPIRATION_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now at the first day of the trial period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(
                DAYS.between(now, startDate.plus(TRIAL_PERIOD)).toInt()
        )))
        assertThat(trialInfo.startDate, Is(equalTo(startDate)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now within the trial period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.plusDays(1)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(
                DAYS.between(now, startDate.plus(TRIAL_PERIOD)).toInt()
        )))
        assertThat(trialInfo.startDate, Is(equalTo(startDate)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now at the last day of the trial period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.plus(TRIAL_PERIOD).minusDays(1)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(
                DAYS.between(now, startDate.plus(TRIAL_PERIOD)).toInt()
        )))
        assertThat(trialInfo.startDate, Is(equalTo(startDate)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now at the first day of the first expired period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.plus(TRIAL_PERIOD)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(-1)))
        assertThat(trialInfo.startDate, Is(equalTo(startDate.plus(TRIAL_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(EXPIRATION_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now within the first expired period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.plus(TRIAL_PERIOD).plusDays(1)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(-1)))
        assertThat(trialInfo.startDate, Is(equalTo(startDate.plus(TRIAL_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(EXPIRATION_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate)))
    }

    @Test
    fun `now at the first day of the second trial period`() {
        val startDate: LocalDate = LocalDate.of(2019, 6, 1)
        val now: LocalDate = startDate.plus(TRIAL_PERIOD).plus(EXPIRATION_PERIOD)

        val fileService = InMemoryTrialFileService(trialStartDate = startDate)
        val trialService = trialService(fileService, now)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.daysUntilExpiration, Is(equalTo(
                DAYS.between(
                        now,
                        startDate.plus(TRIAL_PERIOD).plus(EXPIRATION_PERIOD).plus(TRIAL_PERIOD)).toInt()
        )))
        assertThat(trialInfo.startDate, Is(equalTo(startDate.plus(TRIAL_PERIOD).plus(EXPIRATION_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(startDate.plus(TRIAL_PERIOD).plus(EXPIRATION_PERIOD))))
    }

    private fun trialService(fileService: TrialFileService,
                             now: LocalDate): TrialService {
        val zoneOffset = ZoneOffset.UTC

        return TrialService(
                trialFileService = fileService,
                clock = Clock.fixed(
                        now.atStartOfDay().toInstant(zoneOffset),
                        zoneOffset
                )
        )
    }

    private class InMemoryTrialFileService(private var trialStartDate: LocalDate?) : TrialFileService {

        override fun getTrialStartDate(): LocalDate? = trialStartDate

        override fun setTrialStartDate(localDate: LocalDate) {
            this.trialStartDate = localDate
        }
    }
}
