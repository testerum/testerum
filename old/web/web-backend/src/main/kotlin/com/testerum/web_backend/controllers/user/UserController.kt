package com.testerum.web_backend.controllers.user

import com.testerum.model.user.auth.AuthRequest
import com.testerum.model.user.auth.AuthResponse
import com.testerum.model.user.license.LicenseInfo
import com.testerum.web_backend.services.user.UserFrontendService
import com.testerum.web_backend.util.toFileToUpload
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/user")
class UserController(private val userFrontendService: UserFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = ["/license-info"])
    @ResponseBody
    fun getLicenseInfo(): LicenseInfo {
        return userFrontendService.getLicenseInfo()
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/login/credentials"])
    @ResponseBody
    fun loginWithCredentials(@RequestBody authRequest: AuthRequest): AuthResponse {
        return userFrontendService.loginWithCredentials(authRequest)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/login/file"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun loginWithLicenseFile(@RequestParam("licenseFile") licenseFile: MultipartFile): AuthResponse {
        return userFrontendService.loginWithLicenseFile(
                licenseFile.toFileToUpload()
        )
    }

}
