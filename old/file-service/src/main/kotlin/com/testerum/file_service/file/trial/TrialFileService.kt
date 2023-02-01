package com.testerum.file_service.file.trial

import java.time.LocalDate

interface TrialFileService {

    fun getTrialStartDate(): LocalDate?

    fun setTrialStartDate(localDate: LocalDate)

}
