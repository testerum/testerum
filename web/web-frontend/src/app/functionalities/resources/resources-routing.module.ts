import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ResourcesComponent} from "./resources.component";
import {SetupGuard} from "../../service/guards/setup.guard";
import {ResourceResolver} from "./editors/resource.resolver";
import {StandAlownResourcePanelComponent} from "./editors/infrastructure/form-panel-container/stand-alown-resource-panel.component";

const resourcesRoutes: Routes = [
    {
        path: "automated/resources", component: ResourcesComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: 'create',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }
            },
            {
                path: 'show',
                component: StandAlownResourcePanelComponent, resolve: {resource: ResourceResolver }
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
