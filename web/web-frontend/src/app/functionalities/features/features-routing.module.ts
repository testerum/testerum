import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {FeaturesComponent} from "./features.component";
import {TestEditorComponent} from "./test-editor/test-editor.component";
import {TestResolver} from "./test-editor/test.resolver";
import {LicenseGuard} from "../../service/guards/license-guard.service";
import {FeatureEditorComponent} from "./feature-editor/feature-editor.component";
import {FeatureResolver} from "./feature-editor/feature.resolver";
import {CanDeactivateGuard} from "../../service/guards/CanDeactivateGuard";

const testsRoutes: Routes = [
    {
        path: ":project/features", component: FeaturesComponent, canActivate: [LicenseGuard], canActivateChild: [LicenseGuard],
        children: [
            {
                path: ':action', component: FeatureEditorComponent, resolve: {featureModel: FeatureResolver}, canDeactivate: [CanDeactivateGuard]
            },
            {
                path: 'tests/:action', component: TestEditorComponent, resolve: {testModel: TestResolver}, canDeactivate: [CanDeactivateGuard]
            }
        ]
    },
];
@NgModule({
    imports: [
        RouterModule.forChild(testsRoutes)
    ],
    exports: [RouterModule],
    providers: [],
})
export class FeaturesRoutingModule {
}
