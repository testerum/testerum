import {NgModule} from '@angular/core';

import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {FeedbackComponent} from "./feedback/feedback.component";
import {UserRoutingModule} from "./user-routing.module";
import {ModalModule} from "ngx-bootstrap";
import {FeedbackModalService} from "./feedback/feedback-modal.service";
import {UserProfileComponent} from "./user-profile/user-profile.component";
import {UserProfileModalService} from "./user-profile/user-profile-modal.service";

@NgModule({
    imports: [
        // UserRoutingModule,

        BrowserModule,
        FormsModule,

        ModalModule.forRoot(),

    ],
    exports: [
    ],
    declarations: [
        FeedbackComponent,
        UserProfileComponent,
    ],
    entryComponents: [
        FeedbackComponent,
        UserProfileComponent,
    ],
    providers: [
        FeedbackModalService,
        UserProfileModalService,
    ],
})
export class UserModule { }
