import {Component, OnInit, ViewChild} from '@angular/core';
import {ErrorService} from "../../service/error.service";
import {ModalDirective} from "ngx-bootstrap";
import {ErrorResponse} from "../../model/exception/error-response.model";
import {FullLogErrorResponse} from "../../model/exception/full-log-error-response.model";
import {ValidationErrorResponse} from "../../model/exception/validation-error-response.model";
import {UrlService} from "../../service/url.service";
import {ContextService} from "../../service/context.service";

@Component({
    moduleId: module.id,
    selector: 'error',
    templateUrl: 'error.component.html',
    styleUrls:['error.component.scss']
})
export class ErrorComponent implements OnInit {

    @ViewChild("infoModal") infoModal:ModalDirective;

    title:string = "ERROR";
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
            (error: ErrorResponse) => {
                this.message = "An error occurred";
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
