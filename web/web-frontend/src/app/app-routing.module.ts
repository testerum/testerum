import {RouterModule, Routes} from "@angular/router";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {NgModule} from "@angular/core";
import {SettingsComponent} from "./functionalities/config/settings/settings.component";
import {LicenseGuard} from "./service/guards/license.guard";
import {LicenseComponent} from "./functionalities/config/license/license.component";
import {CurrentProjectGuard} from "./service/guards/current-project.guard";

const appRoutes: Routes = [

    { path: "license", component: LicenseComponent},
    { path: ":project/settings", component: SettingsComponent, canActivate: [LicenseGuard, CurrentProjectGuard]},
    { path: ":project", redirectTo: ":project/features"},
    { path: '**', component: PageNotFoundComponent }
];
@NgModule({
    imports: [
        RouterModule.forRoot(
            appRoutes
        )
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule { }
