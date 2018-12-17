import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {SetupGuard} from "../../service/guards/setup.guard";
import {FeedbackComponent } from "./feedback/feedback.component"


const userProfileRoutes: Routes = [
    {
    },
];
@NgModule({
    imports: [
        RouterModule.forChild(userProfileRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class UserProfileRoutingModule {
}
