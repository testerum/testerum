import {NgModule} from '@angular/core';

import {Routes, RouterModule} from "@angular/router";
import {FeaturesComponent} from "./features.component";
import {TestEditorComponent} from "./test-editor/test-editor.component";
import {TestResolver} from "./test-editor/test.resolver";
import {SetupGuard} from "../../service/guards/setup.guard";
import {FeatureEditorComponent} from "./feature-editor/feature-editor.component";
import {FeatureResolver} from "./feature-editor/feature.resolver";

const testsRoutes: Routes = [
    {
        path: "features", component: FeaturesComponent, canActivate: [SetupGuard], canActivateChild: [SetupGuard],
        children: [
            {
                path: ':action', component: FeatureEditorComponent, resolve: {featureModel: FeatureResolver}
            },
            {
                path: 'tests/:action', component: TestEditorComponent, resolve: {testModel: TestResolver}
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
