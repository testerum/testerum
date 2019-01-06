import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {FeedbackComponent } from "./feedback/feedback.component"


const userRoutes: Routes = [
    {
    },
];
@NgModule({
    imports: [
        RouterModule.forChild(userRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class UserRoutingModule { 
}
