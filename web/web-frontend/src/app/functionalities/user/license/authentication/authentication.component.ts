import {Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild, ViewEncapsulation} from '@angular/core';
import {UserService} from "../../../../service/user.service";
import {AuthRequest} from "../../../../model/user/auth/auth-request.model";
import {AuthResponse} from "../../../../model/user/auth/auth-response.model";
import {ContextService} from "../../../../service/context.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ValidationErrorResponse} from "../../../../model/exception/validation-error-response.model";
import {ErrorCode} from "../../../../model/exception/enums/error-code.enum";
import {FileUpload} from "primeng/primeng";

@Component({
    selector: 'authentication',
    templateUrl: './authentication.component.html',
    styleUrls: ['./authentication.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AuthenticationComponent implements OnInit, OnDestroy {

    @Output("authChange") change = new EventEmitter<void>();

    errorMessage: string;

    email: string;
    password: string;

    @ViewChild("licenseFileUpload") licenseFileUpload: FileUpload;
    licenseFile: File;

    constructor(private userService: UserService,
                private contextService: ContextService) {
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
        this.change.complete()
    }

    onTabChanged() {
        this.errorMessage = "";
    }

    onLogin() {
        this.errorMessage = "";

        let authRequest = new AuthRequest();
        authRequest.email = this.email ? this.email : "";
        authRequest.password = this.password ? this.password : "";
        this.userService.loginWithCredentials(authRequest).subscribe(
            (authResponse: AuthResponse) => {
                this.contextService.setAuthToken(authResponse.authToken);
                this.change.emit();
            },
            (error: HttpErrorResponse) => {
                let validationErrorResponse: ValidationErrorResponse = error.error as ValidationErrorResponse;
                if (validationErrorResponse.errorCode == ErrorCode.CLOUD_OFFLINE || validationErrorResponse.errorCode == ErrorCode.CLOUD_ERROR) {
                    this.errorMessage = "Your application can't reach Testerum server for authentication.\nPlease upload your license file";
                    return;
                }
                if (validationErrorResponse.errorCode == ErrorCode.INVALID_CREDENTIALS) {
                    this.errorMessage = "Invalid username or password";
                    return;
                }
                this.errorMessage = "Authentication not available";
                return;
            }
        );
    }

    onLicenseKeyUpload(event: any): void {
        this.licenseFileUpload.clear();

        for (const file of event.files) {
            this.licenseFile = file;
        }

        this.userService.loginWithLicenseFile(this.licenseFile).subscribe(
            (authResponse: AuthResponse) => {
                this.contextService.setAuthToken(authResponse.authToken);
                this.change.emit();
            },
            (error: HttpErrorResponse) => {
                let validationErrorResponse: ValidationErrorResponse = error.error as ValidationErrorResponse;

                if (validationErrorResponse.errorCode == ErrorCode.INVALID_CREDENTIALS) {
                    this.errorMessage = "Invalid license file";
                    return;
                }
                this.errorMessage = "Authentication not available";
                return;
            }
        );
    }
}
