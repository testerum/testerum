import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {UrlService} from "../../../service/url.service";
import {LicenseService} from "./license.service";
import {AuthRequest} from "./model/authRequest";
import {AuthResponse} from "./model/authResponse";
import {FormUtil} from "../../../utils/form.util";
import {NgForm} from "@angular/forms";
import {HttpErrorResponse} from "@angular/common/http";
import {FileUpload} from "primeng/primeng";
import {MyError} from "../../../model/exception/my-error.model";
import {ErrorCode} from "../../../model/exception/enums/error-code.enum";
import {ValidationErrorResponse} from "../../../model/exception/validation-error-response.model";

@Component({
    selector: 'license',
    templateUrl: 'license.component.html',
    styleUrls: ['license.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class LicenseComponent implements OnInit {

    LICENSE_TYPE_ACTIVATION = "activation";
    LICENSE_TYPE_EVALUATION = "evaluation";

    ACTIVATION_BY_AUTHENTICATION = "activationByAuthentication";
    ACTIVATION_BY_LICENCE_FILE = "activationByLicenseFile";

    EVALUATION_BY_ACCOUNT_CREATION = "evaluationByAccountCreation";
    EVALUATION_BY_AUTHENTICATE = "evaluationByAuthentication";

    errorMessage: string = "";

    licenseType: string = this.LICENSE_TYPE_EVALUATION;
    activationType: string = this.ACTIVATION_BY_AUTHENTICATION;
    evaluationType: string = this.EVALUATION_BY_ACCOUNT_CREATION;

    email: string;
    password: string;
    repeatPassword: string;
    licenseFile: File;

    @ViewChild(NgForm) form: NgForm;
    @ViewChild("licenseFileUpload") licenseFileUpload: FileUpload;

    constructor(private urlService: UrlService,
                private licenseService: LicenseService) {
    }

    ngOnInit() {
        if (this.licenseService.isLoggedIn()) {
            this.urlService.navigateToSetup();
        }
    }

    onBuy(): void {
        this.urlService.navigateToLicenseBuy()
    }

    onLicenseKeyUpload(event: any): void {
        this.licenseFileUpload.clear();

        for (const file of event.files) {
            this.licenseFile = file;
        }
    }

    activate(): void {
        this.errorMessage = "";

        let authRequest = new AuthRequest();
        authRequest.email = this.email;
        authRequest.password = this.password;

        if (this.licenseType == this.LICENSE_TYPE_ACTIVATION && this.activationType == this.ACTIVATION_BY_AUTHENTICATION) {
            if (!this.validateEmail()) {return;}
            if (!this.validatePassword()) {return;}

            this.licenseService.loginWithCredentials(authRequest).subscribe( (license: AuthResponse) => {
                this.licenseService.setLicense(license.authToken);
            });
        }

        if (this.licenseType == this.LICENSE_TYPE_ACTIVATION && this.activationType == this.ACTIVATION_BY_LICENCE_FILE) {
            if (!this.validateLicenseFile()) {return;}

            this.licenseService.loginWithLicenseFile(this.licenseFile).subscribe(
                (license: AuthResponse) => {
                    this.licenseService.setLicense(license.authToken);
                },
                (httpError: HttpErrorResponse) => {
                    FormUtil.setErrorsToForm(this.form, httpError);
                    return null;
                }
            );
        }

        if (this.licenseType == this.LICENSE_TYPE_EVALUATION && this.evaluationType == this.EVALUATION_BY_ACCOUNT_CREATION) {

            if (!this.validateEmail()) {return;}
            if (!this.validatePassword()) {return;}
            if (!this.validateRepeatPassword()) {return;}

            this.licenseService.createTrialAccount(authRequest).subscribe(
                (license: AuthResponse) => {
                    this.licenseService.setLicense(license.authToken);
                },
                (httpError: HttpErrorResponse) => {
                    FormUtil.setErrorsToForm(this.form, httpError);
                    return null;
                }
            );
        }

        if (this.licenseType == this.LICENSE_TYPE_EVALUATION && this.evaluationType == this.EVALUATION_BY_AUTHENTICATE) {
            if (!this.validateEmail()) {return;}
            if (!this.validatePassword()) {return;}

            this.licenseService.loginWithCredentials(authRequest).subscribe(
                (license: AuthResponse) => {
                    this.licenseService.setLicense(license.authToken);
                },
                (httpError: HttpErrorResponse) => {

                        let errorResponse: MyError = httpError.error;

                        if (errorResponse.errorCode.toString() == ErrorCode.CLOUD_ERROR.enumAsString) {
                            let validationException: ValidationErrorResponse = new ValidationErrorResponse().deserialize(errorResponse);
                            this.errorMessage = validationException.validationModel.globalMessage;
                            return;
                        }

                    FormUtil.setErrorsToForm(this.form, httpError);
                    return;
                }
            );
        }
    }

    private validatePassword(): boolean {
        if (!this.password) {
            this.errorMessage = "A password must be set";
            return false;
        }

        if (this.password.length < 6) {
            this.errorMessage = "The password must have at least 6 characters";
            return false;
        }
        return true;
    }

    private validateRepeatPassword(): boolean {
        if (this.password != this.repeatPassword) {
            this.errorMessage = "The Password and Repeat Password fields do not match";
            return false;
        }
        return true;
    }

    private validateEmail(): boolean {
        let EMAIL_REGEXP = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

        if (!this.email || (this.email.length <= 5 || !EMAIL_REGEXP.test(this.email))) {
            this.errorMessage = "Please provide a valid email address";
            return false;
        }
        return true;
    }

    private validateLicenseFile(): boolean {
        if (!this.licenseFile) {
            this.errorMessage = "The license file must be selected";
            return false;
        }
        return true;
    }
}
