import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {LicensePageComponent} from "./license/page/license-page.component";

const userRoutes: Routes = [
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
