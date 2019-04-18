import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {MyError} from "../../../model/exception/my-error.model";
import {FullLogErrorResponse} from "../../../model/exception/full-log-error-response.model";
import {JavaScriptError} from "../../../model/exception/javascript-error.model";
import {Subject} from "rxjs";
import {ErrorFeedback} from "./model/error.feedback";
import {FeedbackService} from "../../../functionalities/user/feedback/feedback.service";

@Component({
    moduleId: module.id,
    selector: 'error-report',
    templateUrl: 'error-feedback-modal.component.html',
    styleUrls:['error-feedback-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ErrorFeedbackModalComponent implements AfterViewInit {

    @ViewChild("errorModal") modal: ModalDirective;

    error: MyError;

    modalComponentRef: ComponentRef<ErrorFeedbackModalComponent>;
    modalSubject:Subject<boolean>;

    model: ErrorFeedback = new ErrorFeedback();

    constructor(private cd: ChangeDetectorRef,
                private feedbackService: FeedbackService) {
    }

    ngAfterViewInit(): void {
        this.init();
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalSubject.complete();

            if (this.modalComponentRef) {
                this.modalComponentRef.destroy();
            }

            this.modalComponentRef = null;
            this.modalSubject = null;
        })
    }

    init() {
        this.model = new ErrorFeedback();

        if(this.error instanceof FullLogErrorResponse) {
            this.model.errorMessage = this.error.uiMessage;
            this.model.errorStacktrace = this.error.exceptionAsString;
        }

        if (this.error instanceof JavaScriptError) {

            if (this.error.error) {
                this.model.errorMessage = this.error.error.message ? this.error.error.message : "";
                this.model.errorStacktrace = this.error.error.stack ? this.error.error.stack : "";
            }
        }

        this.refresh();
    }

    getErrorMessageAndStackTrace(): string {
        let result = "";
        if (this.model.errorMessage) {
            result += "<b>Error Message:</b><br/>";
            result += this.model.errorMessage;
        }
        if (this.model.errorStacktrace) {
            result += "<br/><br/><b>Error StackTrace:</b><br/>";
            result += this.model.errorStacktrace;
        }
        return result;
    }

    close(): void {
        this.modalSubject.next(false);
        this.modal.hide();
    }

    save(): void {
        this.feedbackService.sendErrorFeedback(this.model).subscribe(response => {});
        this.modalSubject.next(true);
        this.modal.hide();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
