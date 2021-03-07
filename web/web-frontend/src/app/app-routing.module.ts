import {RouterModule, Routes} from "@angular/router";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {NgModule} from "@angular/core";
import {SettingsModalComponent} from "./functionalities/config/settings/settings-modal.component";
import {CurrentProjectGuard} from "./service/guards/current-project.guard";
import {NotFundComponent} from "./functionalities/others/not-fund/not-fund.component";
import {LicensePageComponent} from "./functionalities/user/license/page/license-page.component";

const appRoutes: Routes = [

    { path: "license", component: LicensePageComponent},
    { path: ":project/not-found", component: NotFundComponent},
    { path: "not-found", component: NotFundComponent},
    { path: ":project/settings", component: SettingsModalComponent, canActivate: [CurrentProjectGuard]},
    { path: ":project", redirectTo: ":project/project"},
    { path: '**', component: PageNotFoundComponent }
];
@NgModule({
    imports: [
        RouterModule.forRoot(appRoutes, { relativeLinkResolution: 'legacy' })
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule { }
