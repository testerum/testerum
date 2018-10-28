package com.testerum.web_backend.controllers.license

import com.testerum.model.license.AuthRequest
import com.testerum.model.license.AuthResponse
import com.testerum.web_backend.services.license.LicenseFrontendService
import com.testerum.web_backend.util.toFileToUpload
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/license")
open class LicenseController(private val licenseFrontendService: LicenseFrontendService) {

    @RequestMapping (method = [RequestMethod.POST], path = ["/create-trial-account"])
    @ResponseBody
    fun createTrialAccount(@RequestBody authRequest: AuthRequest): AuthResponse {
        return licenseFrontendService.createTrialAccount(authRequest)
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/login/credentials"])
    @ResponseBody
    fun loginWithCredentials(@RequestBody authRequest: AuthRequest): AuthResponse {
        return licenseFrontendService.loginWithCredentials(authRequest)
    }

    @RequestMapping (method = [RequestMethod.POST], path = ["/login/file"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun loginWithLicenseFile(@RequestParam("licenseFile") licenseFile: MultipartFile): AuthResponse {
        return licenseFrontendService.loginWithLicenseFile(
                licenseFile.toFileToUpload()
        )
    }

}
