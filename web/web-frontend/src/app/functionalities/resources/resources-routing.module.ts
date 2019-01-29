import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ResourcesComponent} from "./resources.component";
import {LicenseGuard} from "../../service/guards/license.guard";
import {ResourceResolver} from "./editors/resource.resolver";
import {StandAlownResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alown-resource-panel.component";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";

const resourcesRoutes: Routes = [
    {
        path: ":project/resources", component: ResourcesComponent, canActivate: [LicenseGuard], canActivateChild: [LicenseGuard],
        children: [
            {
                path: 'create',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [UnsavedChangesGuard]
            },
            {
                path: 'show',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [UnsavedChangesGuard]
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
