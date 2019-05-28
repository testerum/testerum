import {AfterViewInit, Component, ComponentRef, OnDestroy, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Feedback} from "./model/feedback.model";
import {InfoModalService} from "../../../generic/components/info_modal/info-modal.service";
import {Subscription} from "rxjs";
import {FeedbackService} from "./feedback.service";
import {ValidationUtil} from "../../../utils/validation.util";

@Component({
  selector: 'feedback.component',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.scss']
})
export class FeedbackComponent implements AfterViewInit, OnDestroy {
    errorMessage: string;
    model: Feedback = new Feedback();

    @ViewChild("feedbackModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<FeedbackComponent>;

    saveFeedbackSubscription: Subscription;
    constructor(private feedbackService: FeedbackService,
                private infoModalService: InfoModalService) {}

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        })
    }

    ngOnDestroy(): void {
        if(this.saveFeedbackSubscription) this.saveFeedbackSubscription.unsubscribe();
    }

    ok() {
        if (!this.hasMessage()) {
            this.errorMessage = "A message is required";
            return;
        }

        if (this.model.email && !ValidationUtil.isValidEmail(this.model.email)) {
            this.errorMessage = "Please provide an valid email address";
            return;
        }

        this.saveFeedbackSubscription = this.feedbackService.sendFeedback(this.model).subscribe( (feedbackResponse: Feedback) => {
            this.infoModalService.showInfoModal(
                "Feedback",
                "We really appreciate the time you took to help us improve Testerum!" +
                " \n Thanks for using Testerum and being an awesome customer!"
            );
            this.modal.hide();
        });
    }

    cancel() {
        this.modal.hide();
    }

    private hasMessage(): boolean {
        if (this.model.message != null) {
            return true;
        }
        return false;
    }
}

