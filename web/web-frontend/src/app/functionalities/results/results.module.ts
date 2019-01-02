import {NgModule} from '@angular/core';

import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ResultsRoutingModule} from "./results-routing.module";
import {AngularSplitModule} from "angular-split-ng6";
import {ResultsTreeComponent} from "./results-tree/results-tree.component";
import {ResultsTreeContainerComponent} from "./results-tree/container/results-tree-container.component";
import {ResultsTreeNodeComponent} from "./results-tree/container/leaf/results-tree-node.component";
import {GenericModule} from "../../generic/generic.module";
import {DndModule} from "ng2-dnd";
import {ResultComponent} from "./main/result/result.component";
import {FeaturesModule} from "../features/features.module";
import {RunnerResultTabsComponent} from "./main/runner-result-tabs.component";
import {TabViewModule} from "primeng/primeng";
import {ResultsComponent} from "./results.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        AngularSplitModule,
        DndModule.forRoot(),

        TabViewModule,

        GenericModule,
        ResultsRoutingModule,
        FeaturesModule,
    ],
    exports: [
        ResultsTreeComponent,
    ],
    declarations: [
        ResultsComponent,
        ResultsTreeComponent,
        ResultsTreeContainerComponent,
        ResultsTreeNodeComponent,
        ResultComponent,
        RunnerResultTabsComponent,
    ],
    entryComponents: [
        ResultsTreeContainerComponent,
        ResultsTreeNodeComponent,
    ],
    providers: [
    ],
})
export class ResultsModule { }
