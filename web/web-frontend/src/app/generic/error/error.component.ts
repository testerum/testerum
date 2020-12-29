import {ChangeDetectorRef, Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ErrorHttpInterceptor} from "../../service/interceptors/error.http-interceptor";
import {MyError} from "../../model/exception/my-error.model";
import {MessageService} from "primeng/api";
import {FullLogErrorResponse} from "../../model/exception/full-log-error-response.model";
import {JavaScriptError} from "../../model/exception/javascript-error.model";
import {ValidationErrorResponse} from "../../model/exception/validation-error-response.model";
import {ContextService} from "../../service/context.service";
import {ErrorFeedbackModalService} from "./report-modal/error-feedback-modal.service";

@Component({
    selector: 'error',
    templateUrl: './error.component.html',
    styleUrls: ['./error.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ErrorComponent implements OnInit {

    constructor(private cd: ChangeDetectorRef,
                private errorService: ErrorHttpInterceptor,
                private contextService: ContextService,
                private messageService: MessageService, //this service is from PrimeNG
                private errorReportModalService: ErrorFeedbackModalService) {
    }

    ngOnInit() {
        this.errorService.errorEventEmitter.subscribe(
            (error: MyError) => {
                if(error instanceof FullLogErrorResponse ||
                    error instanceof JavaScriptError) {

                    this.messageService.add({
                        summary: "Oops... One of our bugs has escaped!",
                        detail: "We are making mistakes too!\n" +
                            "We are very sorry for any inconvenience we may have caused.\n" +
                            "Please report this issue to us and we will fix it as soon as possible.\n" +
                            "We recommend you to refresh the page",
                        data: error,
                        key: "error",
                        sticky: true,
                        severity: "error"
                    });
                }

                if(error instanceof ValidationErrorResponse) {
                    this.messageService.add({
                        summary: "Operation Not Allowed",
                        detail: error.validationModel.globalHtmlMessage ? error.validationModel.globalHtmlMessage : error.validationModel.globalMessage,
                        data: error,
                        key: "validationException",
                        sticky: true,
                        severity: "error"
                    });
                }

                this.refresh();
            }
        )
    }

    report(error: MyError) {
        this.errorReportModalService.showInfoModal(error).subscribe((isReportSubmitted: boolean) => {
            error.isErrorReported = isReportSubmitted;
            this.refresh();
        });
    }

    close(messageKey: string) {
        this.messageService.clear(messageKey);
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    refreshPage() {
        this.contextService.refreshPage();
    }
}
