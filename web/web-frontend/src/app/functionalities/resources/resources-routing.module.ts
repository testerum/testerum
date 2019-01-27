import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ResourcesComponent} from "./resources.component";
import {LicenseGuard} from "../../service/guards/license-guard.service";
import {ResourceResolver} from "./editors/resource.resolver";
import {StandAlownResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alown-resource-panel.component";
import {CanDeactivateGuard} from "../../service/guards/CanDeactivateGuard";

const resourcesRoutes: Routes = [
    {
        path: ":project/resources", component: ResourcesComponent, canActivate: [LicenseGuard], canActivateChild: [LicenseGuard],
        children: [
            {
                path: 'create',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [CanDeactivateGuard]
            },
            {
                path: 'show',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [CanDeactivateGuard]
            }
        ]
    },
];
@NgModule({
    imports: [
        RouterModule.forChild(resourcesRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class ResourcesRoutingModule {
}
