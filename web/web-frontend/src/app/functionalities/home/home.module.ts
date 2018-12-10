import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HomeComponent} from './home.component';
import {HomeRoutingModule} from "./home-routing.module";
import {GenericModule} from "../../generic/generic.module";

@NgModule({
    declarations: [HomeComponent],
    imports: [
        CommonModule,

        GenericModule,

        HomeRoutingModule,
    ]
})
export class HomeModule {
}
