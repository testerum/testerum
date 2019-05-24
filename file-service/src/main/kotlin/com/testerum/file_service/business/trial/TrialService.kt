package com.testerum.file_service.business.trial

import com.testerum.file_service.file.trial.TrialFileService
import com.testerum.model.user.license.TrialLicenceInfo
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

    fun getTrialInfo(): TrialLicenceInfo {
        synchronized(lock) {
            val now: LocalDate = LocalDate.now(clock)
            var trialStartDate: LocalDate? = trialFileService.getTrialStartDate()

            if (trialStartDate == null) {
                // first time we start an trial
                trialStartDate = now
                trialFileService.setTrialStartDate(trialStartDate)
            }

            if (now < trialStartDate) {
                return TrialLicenceInfo(
                        startDate = trialStartDate.minus(EXPIRATION_PERIOD),
                        endDate = trialStartDate,
                        daysUntilExpiration = -1,
                        expired = true
                )
            }

            // iterate over the alternating periods of TRIAL / EXPIRED until now is between start (inclusive) and end (exclusive)
            var trialInfo = TrialLicenceInfo(
                    startDate = trialStartDate,
                    endDate = trialStartDate.plus(TRIAL_PERIOD),
                    daysUntilExpiration = now.until(trialStartDate.plus(TRIAL_PERIOD)).days,
                    expired = false
            )

            var newTrialStartDate = trialStartDate
            while (!(trialInfo.startDate <= now && now < trialInfo.endDate)) {
                trialInfo = trialInfo.next()

                if (!trialInfo.expired) {
                    newTrialStartDate = trialInfo.startDate
                }
            }

            // save the new trial date
            if (newTrialStartDate != null && newTrialStartDate != trialStartDate) {
                trialFileService.setTrialStartDate(newTrialStartDate)
            }

            return trialInfo
        }
    }

    private fun TrialLicenceInfo.next(): TrialLicenceInfo {
        return if (expired) {
            TrialLicenceInfo(
                    startDate = endDate,
                    endDate = endDate.plus(TRIAL_PERIOD),
                    daysUntilExpiration = TRIAL_PERIOD.days,
                    expired = false
            )
        } else {
            TrialLicenceInfo(
                    startDate = endDate,
                    endDate = endDate.plus(EXPIRATION_PERIOD),
                    daysUntilExpiration = -1,
                    expired = true
            )
        }
    }

    private fun TrialLicenceInfo.previous(): TrialLicenceInfo {
        return if (expired) {
            TrialLicenceInfo(
                    startDate = startDate.minus(TRIAL_PERIOD),
                    endDate = startDate,
                    daysUntilExpiration = TRIAL_PERIOD.days,
                    expired = false
            )
        } else {
            TrialLicenceInfo(
                    startDate = startDate.minus(EXPIRATION_PERIOD),
                    endDate = startDate,
                    daysUntilExpiration = -1,
                    expired = true
            )
        }
    }

}
