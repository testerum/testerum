package com.testerum.file_service.file.trial

import com.testerum.common_encrypted_prefs.EncryptedPrefs
import com.testerum.common_encrypted_prefs.getLocalDate
import com.testerum.common_encrypted_prefs.setLocalDate
import com.testerum.file_service.business.trial.TrialService
import java.time.LocalDate
import java.util.Base64

class JavaPreferencesTrialFileService : TrialFileService {

    companion object {
        private val SECRET_KEY: ByteArray = Base64.getDecoder().decode(
                "HZ2254ia5xhTWfWGI67tAaY8/8BhZAd7P7xwB1lXbD2ePpIZn2vApFdXjztjj+hn" +
                        "MzQaeO61s0GAESHHU+wi48iqX8Ilzc7APgu9OpRuBf6tAYw/8ysYN4Kcg4hiabog" +
                        "mGC7Gjh0uUWpTtD8kpiytroE1fLteZreMFq/n6yLAXGFOvlBlkGLYd204W13unhI" +
                        "eK6zEV/gHUgH04uJ5irZTk+bAIfaqTEAK3KQSuZxBAMvKd4H6a/Z6X+cPuPrLfuG" +
                        "8X9woP883s81v2gMG/Suj5VeljVdpIfT/h/g1dO0jl6Wh1F4ICcr4sFtwQSijFIw" +
                        "VoRgpE0mjijwmJsOSZjMggwGLvJtc4rtTinD9M1yB1f/RT8YKsoTGL/Qrr6MSIm3" +
                        "Ba84muxzUdR5ztx55fdgQrsJ9+EdYZbC6C97CvdAMgDmgP7bbMU+Qxh7P4951pwa" +
                        "y0QwOMYuIazHXEWajhJFHyQvxcQ4a7C4hkLd3BOvJXb3a0qpg4/XQW48SDBcnDyw" +
                        "8xQo4HlNBBZB3hDYClkeryW+GdFkqq0/4TkfaLquT+powkr2/V1uyiK+3p1D6XzS" +
                        "zKUtlZl9ZVYEeoQN48VOeMLsF6O96FbzuWBoI0sl2ITP0cKWLbzc2wDSDOQfPZ19" +
                        "jMTLzd0vFtWVjznwndVr2Y683BU47Z9SMNEC+pmPtqtUvDIIvX0L5F+P45E9fxot" +
                        "4FlX/4a5U73XH6u+NHiP/AsEj2/0w1baeRtpYwITyhchKmZdCMvEkBkoV+/P3QMg" +
                        "W+aCg1JXSqe4mpwpO9vO4vWgEGsylAg1fKBwRyaVbSOGEs7zOD7da3Yzl2uHVx8y" +
                        "zKUbjJHkiI2Mjv9Pe1LyEYuU5enhk/6byWtKLq8S67laOQKA3qatLirXs6nFIDbX" +
                        "s19ND2ibouQMsR8XyQUSRlOHc43gyTv6MY9Wr4yPBFfGYsWETbqVuabVl0x0gEUe" +
                        "/7rrnrNsdnITDDd932BsWw7ricimaFgbY3Hp54r3okhLtrKRgszXTDNqqoHsCK4z" +
                        "w/zkMLqVjQh7P/MmAhElKD6R4ktHtGO2+mlgvNATboxYJvv8nNtB4g4LW3WngH1U" +
                        "3YiSHY9H/uPoIUHUFkGeWydGWgzWpg5YZYJmQSXrEmzOOZh7e8sict8XjLUVYYRM" +
                        "yQv7zsY8T+ZKCLf8osgeHaUs+DUVeRuVsPSLrSDJsrxhDzrAYfrKBkBaRLPsOKrV" +
                        "0VR/ObKVnTvpS5MdE4/mcEvsfBwP2NlA1OaUNyIlsNoQ5AzBDRAta6Q7yF6+U2Cw" +
                        "MYeD9Pc0q4cxgn0ihqp3/gJTYrT38V/ib1QTmAergcLcGIOpZeKeWsaTCWG9IXvq" +
                        "dmjH/EyK18CEmOt1e0Nqgw=="
        )

        private const val KEY_EVALUATION_START = "evaluationStart"

    }

    private val licenseNode = EncryptedPrefs.userRoot(SECRET_KEY).node("/com/testerum/license")


    override fun getTrialStartDate(): LocalDate? = licenseNode.getLocalDate(KEY_EVALUATION_START)

    override fun setTrialStartDate(localDate: LocalDate) = licenseNode.setLocalDate(KEY_EVALUATION_START, localDate)

}
