import {RouterModule, Routes} from "@angular/router";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {NgModule} from "@angular/core";
import {SettingsComponent} from "./functionalities/config/settings/settings.component";
import {LicenseGuard} from "./service/guards/license.guard";
import {LicenseComponent} from "./functionalities/config/license/license.component";

const appRoutes: Routes = [

    { path: "license", component: LicenseComponent},
    { path: ":project/settings", component: SettingsComponent, canActivate: [LicenseGuard]},
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
