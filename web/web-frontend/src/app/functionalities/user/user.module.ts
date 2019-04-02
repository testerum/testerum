import {NgModule} from '@angular/core';

import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {FeedbackComponent} from "./feedback/feedback.component";
import {ModalModule} from "ngx-bootstrap";
import {FeedbackModalService} from "./feedback/feedback-modal.service";
import {LicenseModalComponent} from "./license/license-modal.component";
import {LicenseModalService} from "./license/license-modal.service";
import {AuthenticationComponent} from './license/authentication/authentication.component';
import {TabViewModule} from 'primeng/tabview';
import {PasswordModule} from 'primeng/password';
import {ButtonModule} from 'primeng/button';
import {FileUploadModule} from 'primeng/fileupload';
import {CheckboxModule} from 'primeng/checkbox';

@NgModule({
    imports: [
        // UserRoutingModule,

        BrowserModule,
        FormsModule,
        TabViewModule,
        PasswordModule,
        ButtonModule,
        FileUploadModule,
        CheckboxModule,

        ModalModule.forRoot(),

    ],
    exports: [
    ],
    declarations: [
        FeedbackComponent,
        LicenseModalComponent,
        AuthenticationComponent,
    ],
    entryComponents: [
        FeedbackComponent,
        LicenseModalComponent,
    ],
    providers: [
        FeedbackModalService,
        LicenseModalService,
    ]
})
export class UserModule { }
