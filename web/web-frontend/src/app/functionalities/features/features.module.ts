import { NgModule } from '@angular/core';

import {FeaturesRoutingModule} from "./features-routing.module";
import {GenericModule} from "../../generic/generic.module";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ModalModule, CollapseModule} from "ngx-bootstrap";
import {DndModule} from "ng2-dnd";
import {FeaturesComponent} from "./features.component";
import {TestsService} from "../../service/tests.service";
import {TestEditorComponent} from "./test-editor/test-editor.component";
import {StepsModule} from "../steps/steps.module";
import {TestResolver} from "./test-editor/test.resolver";
import {AngularSplitModule} from "angular-split";
import {TestsRunnerComponent} from "./tests-runner/tests-runner.component";
import {TestsRunnerService} from "./tests-runner/tests-runner.service";
import {RunnerTreeService} from "./tests-runner/tests-runner-tree/runner-tree.service";
import {RunnerTreeComponent} from "./tests-runner/tests-runner-tree/runner-tree.component";
import {RunnerTreeNodeComponent} from "./tests-runner/tests-runner-tree/runner-tree-node/runner-tree-node.component";
import {TestsRunnerLogsComponent} from "./tests-runner/tests-runner-logs/tests-runner-logs.component";
import {TestsRunnerLogsService} from "./tests-runner/tests-runner-logs/tests-runner-logs.service";
import {FeaturesTreeService} from "./features-tree/features-tree.service";
import {TestNodeComponent} from "./features-tree/container/node/test-node.component";
import {FeatureContainerComponent} from "./features-tree/container/feature-container.component";
import {TooltipModule} from "primeng/primeng";
import {FeatureResolver} from "./feature-editor/feature.resolver";
import {FeatureEditorComponent} from "./feature-editor/feature-editor.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        FeaturesRoutingModule,
        ModalModule.forRoot(),
        CollapseModule.forRoot(),
        DndModule.forRoot(),
        AngularSplitModule,

        TooltipModule,

        GenericModule,
        StepsModule,
    ],
    exports: [
        FeaturesComponent,
        RunnerTreeComponent,
        TestsRunnerLogsComponent,
    ],
    declarations: [
        FeaturesComponent,
        FeatureContainerComponent,
        TestNodeComponent,
        TestEditorComponent,

        TestsRunnerComponent,
        RunnerTreeComponent,
        RunnerTreeNodeComponent,
        TestsRunnerLogsComponent,

        FeatureEditorComponent,
    ],
    entryComponents: [

    ],
    providers: [
        TestsService,
        TestResolver,

        FeaturesTreeService,
        TestsRunnerService,
        RunnerTreeService,
        TestsRunnerLogsService,

        FeatureResolver,

    ],
})
export class FeaturesModule { }
