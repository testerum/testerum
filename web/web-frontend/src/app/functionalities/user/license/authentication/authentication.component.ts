import {
    ChangeDetectorRef,
    Component,
    EventEmitter,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
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
export class AuthenticationComponent implements OnDestroy {

    @Output("authChange") change = new EventEmitter<void>();

    errorMessage: string;

    email: string;
    password: string;

    @ViewChild("licenseFileUpload") licenseFileUpload: FileUpload;
    licenseFile: File;

    constructor(private cd: ChangeDetectorRef,
                private userService: UserService,
                private contextService: ContextService) {
    }

    ngOnDestroy(): void {
        this.change.complete()
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    onTabChanged() {
        this.errorMessage = "";
    }

    isLoginValid(): boolean {
        if (this.email && this.password) {
            return true;
        }
        return false;
    }

    onLogin() {
        if (!this.isLoginValid()) {
            return;
        }

        this.errorMessage = "";

        let authRequest = new AuthRequest();
        authRequest.email = this.email ? this.email : "";
        authRequest.password = this.password ? this.password : "";
        this.userService.loginWithCredentials(authRequest).subscribe(
            (authResponse: AuthResponse) => {
                this.contextService.license.setAuthToken(authResponse.authToken, authResponse.currentUserLicense);
                this.change.emit();
            },
            (error: HttpErrorResponse) => {
                let validationErrorResponse: ValidationErrorResponse = error.error as ValidationErrorResponse;
                if (validationErrorResponse.errorCode == ErrorCode.CLOUD_OFFLINE || validationErrorResponse.errorCode == ErrorCode.CLOUD_ERROR) {
                    this.errorMessage = "Your application can't reach Testerum server for authentication.\nPlease upload your license file";
                    this.refresh();
                    return;
                }
                if (validationErrorResponse.errorCode == ErrorCode.INVALID_CREDENTIALS) {
                    this.errorMessage = "Invalid username or password";
                    this.refresh();
                    return;
                }
                if (validationErrorResponse.errorCode == ErrorCode.NO_VALID_LICENSE) {
                    this.errorMessage = "This user doesn't have any valid license.";
                    this.refresh();
                    return;
                }
                this.errorMessage = "Authentication not available";
                this.refresh();
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
                this.contextService.license.setAuthToken(authResponse.authToken, authResponse.currentUserLicense);
                this.change.emit();
            },
            (error: HttpErrorResponse) => {
                let validationErrorResponse: ValidationErrorResponse = error.error as ValidationErrorResponse;

                if (validationErrorResponse.errorCode == ErrorCode.INVALID_CREDENTIALS || validationErrorResponse.errorCode == ErrorCode.NO_VALID_LICENSE) {
                    this.errorMessage = "Invalid license file";
                    this.refresh();
                    return;
                }
                this.errorMessage = "Authentication not available";
                this.refresh();
                return;
            }
        );
    }
}
