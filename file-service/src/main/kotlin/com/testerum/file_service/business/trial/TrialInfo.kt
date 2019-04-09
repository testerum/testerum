package com.testerum.file_service.business.trial

import java.time.LocalDate

data class TrialInfo(val status: TrialStatus,
                     val startDate: LocalDate,
                     val endDate: LocalDate)
