package com.testerum.web_backend.controllers.user_profile

import com.testerum.model.license.profile.TrialUserProfile
import com.testerum.model.license.profile.UserProfile
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/user-profile")
class UserProfileController {

    @RequestMapping(method = [RequestMethod.POST], path = ["/feedback"])
    @ResponseBody
    fun getWarnings(@RequestBody userProfile: UserProfileController): UserProfileController {
        //TODO Cristi: implement this method
        return userProfile;
    }

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getCurrentUserProfile(): UserProfile {
        val startDate = LocalDate.now().minusDays(10)
        val endDate = startDate.plusMonths(1)

        return TrialUserProfile(startDate, endDate)
    }

}
