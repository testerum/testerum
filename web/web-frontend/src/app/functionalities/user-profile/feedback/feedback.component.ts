import {AfterViewInit, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {Feedback} from "./model/feedback.model";

@Component({
  selector: 'feedback.component',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.scss']
})
export class FeedbackComponent implements AfterViewInit {
    errorMessage: string;
    model: Feedback = new Feedback();

    @ViewChild("feedbackModal") modal:ModalDirective;
    modalComponentRef: ComponentRef<FeedbackComponent>;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {

            this.modalComponentRef.destroy();

            this.modalComponentRef = null;
        })
    }

    ok() {
        if (!this.hasSubjectOrDescription()) {
            this.errorMessage = "A subject or a description is required";
            return;
        }

        this.modal.hide();
    }

    cancel() {
        this.modal.hide();
    }

    private hasSubjectOrDescription(): boolean {
        if (this.model.description != null) {
            return true;
        }
        if (this.model.subject != null) {
            return true;
        }
        return false;
    }
}
