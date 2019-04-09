package com.testerum.file_service.business.trial

import com.testerum.file_service.file.trial.TrialFileService
import java.time.Clock
import java.time.LocalDate
import java.time.Period

class TrialService(private val trialFileService: TrialFileService,
                   private val clock: Clock) {

    companion object {
        val TRIAL_PERIOD: Period = Period.ofDays(15)
        val EXPIRATION_PERIOD: Period = Period.ofMonths(3)
    }

    private val lock = Object()

    fun getTrialInfo(): TrialInfo {
        synchronized(lock) {
            val now: LocalDate = LocalDate.now(clock)
            var trialStartDate: LocalDate? = trialFileService.getTrialStartDate()

            if (trialStartDate == null) {
                // first time we start an trial
                trialStartDate = now
                trialFileService.setTrialStartDate(trialStartDate)
            }

            if (now < trialStartDate) {
                return TrialInfo(
                        status = TrialStatus.TRIAL_EXPIRED,
                        startDate = trialStartDate.minus(EXPIRATION_PERIOD),
                        endDate = trialStartDate
                )
            }

            // iterate over the alternating periods of TRIAL / EXPIRED until now is between start (inclusive) and end (exclusive)
            var trialInfo = TrialInfo(
                    status = TrialStatus.IN_TRIAL,
                    startDate = trialStartDate,
                    endDate = trialStartDate.plus(TRIAL_PERIOD)
            )

            var newTrialStartDate = trialStartDate
            while (!(trialInfo.startDate <= now && now < trialInfo.endDate)) {
                trialInfo = trialInfo.next()

                if (trialInfo.status == TrialStatus.IN_TRIAL) {
                    newTrialStartDate = trialInfo.startDate
                }
            }

            if (newTrialStartDate != null && newTrialStartDate != trialStartDate) {
                trialFileService.setTrialStartDate(newTrialStartDate)
            }

            return trialInfo
        }
    }

    private fun TrialInfo.next(): TrialInfo {
        return when (status) {
            TrialStatus.IN_TRIAL -> TrialInfo(
                    status = TrialStatus.TRIAL_EXPIRED,
                    startDate = endDate,
                    endDate = endDate.plus(EXPIRATION_PERIOD)
            )

            TrialStatus.TRIAL_EXPIRED -> TrialInfo(
                    status = TrialStatus.IN_TRIAL,
                    startDate = endDate,
                    endDate = endDate.plus(TRIAL_PERIOD)
            )
        }
    }
}
