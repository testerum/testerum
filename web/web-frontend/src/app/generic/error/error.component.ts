import {Component, OnInit, ViewChild} from '@angular/core';
import {ErrorService} from "../../service/error.service";
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
    styleUrls:['error.component.scss']
})
export class ErrorComponent implements OnInit {

    @ViewChild("infoModal") infoModal:ModalDirective;

    message:string;
    details:string;
    shouldShowDetails: boolean = false;
    shouldRefreshPage: boolean = true;

    constructor(private errorService: ErrorService,
                private urlService: UrlService,
                private contextService: ContextService) {
    }

    ngOnInit() {
        this.errorService.errorEventEmitter.subscribe(
            (error: MyError) => {
                this.message = "Oops... One of our bugs has escaped!";
                this.details = null;
                this.shouldShowDetails = false;
                this.shouldRefreshPage = true;

                if(error instanceof FullLogErrorResponse) {
                    this.message = error.uiMessage;
                    this.details = error.exceptionAsString;
                }

                if(error instanceof ValidationErrorResponse) {
                    this.message = error.validationModel.globalValidationMessage;
                    this.details = error.validationModel.globalValidationMessageDetails;
                    this.shouldRefreshPage = false;
                }

                if (error instanceof JavaScriptError) {

                    if (error.error) {
                        let exceptionDetails = "Exception: " + (error.error.message ? error.error.message : null) + "\n";
                        exceptionDetails += error.error.stack;

                        this.details = exceptionDetails;
                    }
                }

                this.show();
            }
        )
    }

    show(): void {
        this.infoModal.show();
    }

    toggleShowDetails(): void {
        this.shouldShowDetails = !this.shouldShowDetails;
    }

    close(): void {
        this.infoModal.hide();
        if (this.shouldRefreshPage) {
            if (this.contextService.getProjectName()) {
                window.location.href = window.location.protocol + "//" + window.location.host + "/" + this.contextService.getProjectName() + "/" ;
            } else {
                window.location.href = window.location.protocol + "//" + window.location.host + "/";
            }
        }
    }
}
