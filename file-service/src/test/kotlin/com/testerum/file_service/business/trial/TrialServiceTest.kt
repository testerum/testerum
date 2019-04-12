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
import org.hamcrest.Matchers.`is` as Is

class TrialServiceTest {

    companion object {
        private val NOW: LocalDate = LocalDate.of(2019, 4, 11)
    }

    @Test
    fun `trial period not yet started - the trial period will begin`() {
        val fileService = InMemoryTrialFileService(trialStartDate = null)
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.startDate, Is(equalTo(NOW)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW)))
    }

    @Test
    fun `now before trial started - in expired period`() {
        val fileService = InMemoryTrialFileService(trialStartDate = NOW.plusDays(1))
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.startDate, Is(equalTo(NOW.plusDays(1).minus(EXPIRATION_PERIOD).minus(TRIAL_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW.plusDays(1))))
    }

    @Test
    fun `now within the trial period`() {
        val fileService = InMemoryTrialFileService(trialStartDate = NOW.minusDays(1))
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.startDate, Is(equalTo(NOW.minusDays(1))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW.minusDays(1))))
    }

    @Test
    fun `now at the last day of the trial period`() {
        val fileService = InMemoryTrialFileService(trialStartDate = NOW.minus(TRIAL_PERIOD))
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.startDate, Is(equalTo(trialInfo.endDate.minus(TRIAL_PERIOD))))
        assertThat(trialInfo.endDate, Is(equalTo(NOW)))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW.minus(TRIAL_PERIOD))))
    }

    @Test
    fun `now within the first expired period`() {
        val fileService = InMemoryTrialFileService(trialStartDate = NOW.minus(TRIAL_PERIOD).minusDays(1))
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(true)))
        assertThat(trialInfo.startDate, Is(equalTo(NOW.minus(TRIAL_PERIOD).minusDays(1))))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW.minus(TRIAL_PERIOD).minusDays(1))))
    }

    @Test
    fun `now at the first day of the second trial period`() {
        val fileService = InMemoryTrialFileService(trialStartDate = NOW.minus(TRIAL_PERIOD).minus(EXPIRATION_PERIOD))
        val trialService = trialService(fileService)

        val trialInfo = trialService.getTrialInfo()

        assertThat(trialInfo.expired, Is(equalTo(false)))
        assertThat(trialInfo.startDate, Is(equalTo(NOW)))
        assertThat(trialInfo.endDate, Is(equalTo(trialInfo.startDate.plus(TRIAL_PERIOD))))

        assertThat(fileService.getTrialStartDate(), Is(equalTo(NOW)))
    }

    private fun trialService(fileService: TrialFileService): TrialService {
        val zoneOffset = ZoneOffset.UTC

        return TrialService(
                trialFileService = fileService,
                clock = Clock.fixed(
                        NOW.atStartOfDay().toInstant(zoneOffset),
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
