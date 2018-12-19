package com.testerum.web_backend.controllers.user_profile

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/userProfile")
class UserProfileController {

    @RequestMapping(method = [RequestMethod.POST], path = ["/feedback"])
    @ResponseBody
    fun getWarnings(@RequestBody userProfile: UserProfileController): UserProfileController {
//TODO Cristi: implement this method
        return userProfile;
    }
}