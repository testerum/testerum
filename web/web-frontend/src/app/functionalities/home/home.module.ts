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
    ],
    entryComponents: [
        CreateProjectComponent
    ],
    providers: [
        CreateProjectService,
    ]
})
export class HomeModule {
}
