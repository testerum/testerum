import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ErrorHttpInterceptor} from "../../service/interceptors/error.http-interceptor";
import {ModalDirective} from "ngx-bootstrap";
import {MyError} from "../../model/exception/my-error.model";
import {FullLogErrorResponse} from "../../model/exception/full-log-error-response.model";
import {ValidationErrorResponse} from "../../model/exception/validation-error-response.model";
import {UrlService} from "../../service/url.service";
import {ContextService} from "../../service/context.service";
import {JavaScriptError} from "../../model/exception/javascript-error.model";

@Component({
    moduleId: module.id,
    selector: 'error',
    templateUrl: 'error.component.html',
    styleUrls:['error.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ErrorComponent implements OnInit {

    @ViewChild("errorModal") modal: ModalDirective;

    title: string;
    message:string;
    details:string;
    shouldShowDetails: boolean = false;
    shouldRefreshPage: boolean = true;
    isOurBug: boolean = true;

    constructor(private cd: ChangeDetectorRef,
                private errorService: ErrorHttpInterceptor,
                private urlService: UrlService,
                private contextService: ContextService) {
    }

    ngOnInit() {
        this.errorService.errorEventEmitter.subscribe(
            (error: MyError) => {
                this.title = "Exception";
                this.message = "Oops... One of our bugs has escaped!";
                this.details = null;
                this.shouldShowDetails = false;
                this.shouldRefreshPage = true;
                this.isOurBug = true;

                if(error instanceof FullLogErrorResponse) {
                    this.message = error.uiMessage;
                    this.details = error.exceptionAsString;
                }

                if(error instanceof ValidationErrorResponse) {
                    this.title = "Validation Exception";
                    this.message = error.validationModel.globalHtmlMessage ? error.validationModel.globalHtmlMessage : error.validationModel.globalMessage;
                    this.details = error.validationModel.globalMessageDetails;
                    this.shouldRefreshPage = false;
                    this.isOurBug = false;
                }

                if (error instanceof JavaScriptError) {

                    if (error.error) {
                        let exceptionDetails = "Exception: " + (error.error.message ? error.error.message : "") + "\n";
                        exceptionDetails += (error.error.stack ? error.error.stack : "");

                        this.details = exceptionDetails;
                    }
                }

                this.refresh();
                this.show();
            }
        )
    }

    show(): void {
        this.modal.show();
    }

    toggleShowDetails(): void {
        this.shouldShowDetails = !this.shouldShowDetails;
    }

    close(): void {
        this.modal.hide();
        if (this.shouldRefreshPage) {
            if (this.contextService.isProjectSelected()) {
                window.location.href = window.location.protocol + "//" + window.location.host + "/" + this.contextService.getProjectName() + "/" ;
            } else {
                window.location.href = window.location.protocol + "//" + window.location.host + "/";
            }
        }
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
