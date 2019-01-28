import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home.component";
import {MultipleProjectsFoundComponent} from "./alerts/multiple-projects-found/multiple-projects-found.component";
import {NoProjectFoundComponent} from "./alerts/no-project-found/no-project-found.component";

const homeRoutes: Routes = [
    { path: "", component: HomeComponent },
    { path: "multipleProjectsFound", component: MultipleProjectsFoundComponent},
    { path: "noProjectFound", component: NoProjectFoundComponent},
];
@NgModule({
    imports: [
        RouterModule.forChild(homeRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class HomeRoutingModule {
}
