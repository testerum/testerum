import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HomeComponent} from './home.component';
import {HomeRoutingModule} from "./home-routing.module";
import {GenericModule} from "../../generic/generic.module";
import {BrowserModule} from "@angular/platform-browser";
import {CreateProjectService} from "./create-project/create-project.service";
import {CreateProjectComponent} from "./create-project/create-project.component";
import {ModalModule} from "ngx-bootstrap";
import {FormsModule} from "@angular/forms";
import {MultipleProjectsFoundComponent} from "./alerts/multiple-projects-found/multiple-projects-found.component";
import {SelectProjectModalComponent} from "./alerts/multiple-projects-found/select-project-modal/select-project-modal.component";
import { NoProjectFoundComponent } from './alerts/no-project-found/no-project-found.component';
import { BigLogoComponent } from './alerts/big-logo/big-logo.component';

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        FormsModule,
        ModalModule.forRoot(),

        GenericModule,

        HomeRoutingModule,
    ],
    declarations: [
        HomeComponent,
        CreateProjectComponent,
        MultipleProjectsFoundComponent,
        SelectProjectModalComponent,
        NoProjectFoundComponent,
        BigLogoComponent,
    ],
    entryComponents: [
        CreateProjectComponent,
        SelectProjectModalComponent,
    ],
    providers: [
        CreateProjectService,
    ]
})
export class HomeModule {
}
