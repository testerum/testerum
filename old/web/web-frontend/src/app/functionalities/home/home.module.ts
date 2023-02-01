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
import {SelectProjectModalComponent} from "./multiple-projects-found/select-project-modal.component";
import {SelectProjectModalService} from "./multiple-projects-found/select-project-modal.service";
import { BubbleTipComponent } from './bubble-tip/bubble-tip.component';

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
        SelectProjectModalComponent,
        BubbleTipComponent,
    ],
    entryComponents: [
        CreateProjectComponent,
        SelectProjectModalComponent,
    ],
    providers: [
        CreateProjectService,
        SelectProjectModalService,
    ]
})
export class HomeModule {
}
