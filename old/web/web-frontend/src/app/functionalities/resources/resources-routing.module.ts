import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ResourcesComponent} from "./resources.component";
import {ResourceResolver} from "./editors/resource.resolver";
import {StandAloneResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alone-resource-panel.component";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";
import {CurrentProjectGuard} from "../../service/guards/current-project.guard";

const resourcesRoutes: Routes = [
    {
        path: ":project/resources", component: ResourcesComponent, canActivate: [CurrentProjectGuard],
        children: [
            {
                path: 'create',
                component: StandAloneResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [UnsavedChangesGuard]
            },
            {
                path: 'show',
                component: StandAloneResourcePanelComponent, resolve: {resource: ResourceResolver }, canDeactivate: [UnsavedChangesGuard]
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
