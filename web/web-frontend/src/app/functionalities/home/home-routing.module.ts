import {NgModule} from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home.component";
import {LicenseGuard} from "../../service/guards/license.guard";

const homeRoutes: Routes = [
    { path: "", component: HomeComponent, canActivate: [LicenseGuard], canActivateChild: [LicenseGuard]},
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
