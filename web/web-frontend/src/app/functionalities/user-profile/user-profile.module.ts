import {NgModule} from '@angular/core';

import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {FeedbackComponent} from "./feedback/feedback.component";
import {UserProfileRoutingModule} from "./user-profile-routing.module";
import {ModalModule} from "ngx-bootstrap";
import {FeedbackModalService} from "./feedback/feedback-modal.service";
import {AboutComponent} from "./about/about.component";
import {AboutModalService} from "./about/about-modal.service";

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
        AboutComponent,
    ],
    entryComponents: [
        FeedbackComponent,
        AboutComponent,
    ],
    providers: [
        FeedbackModalService,
        AboutModalService,
    ],
})
export class UserProfileModule { }
