import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";

const configRoutes: Routes = [
];
@NgModule({
    imports: [
        RouterModule.forChild(configRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class ConfigRoutingModule {
}
