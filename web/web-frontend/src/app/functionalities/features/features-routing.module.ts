import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {FeaturesComponent} from "./features.component";
import {TestEditorComponent} from "./test-editor/test-editor.component";
import {TestResolver} from "./test-editor/test.resolver";
import {LicenseGuard} from "../../service/guards/license.guard";
import {FeatureEditorComponent} from "./feature-editor/feature-editor.component";
import {FeatureResolver} from "./feature-editor/feature.resolver";
import {UnsavedChangesGuard} from "../../service/guards/unsaved-changes.guard";
import {CurrentProjectGuard} from "../../service/guards/current-project.guard";

const testsRoutes: Routes = [
    {
        path: ":project/project", component: FeaturesComponent, canActivate: [LicenseGuard, CurrentProjectGuard], canActivateChild: [LicenseGuard],
        children: [
            {
                path: ':action', component: FeatureEditorComponent, resolve: {featureModel: FeatureResolver}, canDeactivate: [UnsavedChangesGuard]
            },
            {
                path: 'tests/:action', component: TestEditorComponent, resolve: {testModel: TestResolver}, canDeactivate: [UnsavedChangesGuard]
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
