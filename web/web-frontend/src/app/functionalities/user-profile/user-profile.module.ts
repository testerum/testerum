import {NgModule} from '@angular/core';

import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {FeedbackComponent} from "./feedback/feedback.component";
import {UserProfileRoutingModule} from "./user-profile-routing.module";
import {ModalModule} from "ngx-bootstrap";
import {FeedbackModalService} from "./feedback/feedback-modal.service";

@NgModule({
    imports: [
        // UserProfileRoutingModule,

        BrowserModule,
        FormsModule,

        ModalModule.forRoot(),

    ],
    exports: [
    ],
    declarations: [
        FeedbackComponent,
    ],
    entryComponents: [
        FeedbackComponent,
    ],
    providers: [
        FeedbackModalService,
    ],
})
export class UserProfileModule { }
