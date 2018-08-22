import {RouterModule, Routes} from "@angular/router";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {NgModule} from "@angular/core";
import {SetupComponent} from "./functionalities/config/setup/setup.component";
import {SettingsComponent} from "./functionalities/config/settings/settings.component";
import {SetupGuard} from "./service/guards/setup.guard";

const appRoutes: Routes = [

    { path: '',   redirectTo: '/features', pathMatch: 'full' },
    { path: "setup", component: SetupComponent},
    { path: "settings", component: SettingsComponent, canActivate: [SetupGuard]},
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
