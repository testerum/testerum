package com.testerum.web_backend.controllers.user

import com.testerum.model.license.auth.AuthRequest
import com.testerum.model.license.auth.AuthResponse
import com.testerum.model.license.info.LicenseInfo
import com.testerum.web_backend.services.user.UserFrontendService
import com.testerum.web_backend.util.toFileToUpload
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
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
