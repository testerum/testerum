import { NgModule } from '@angular/core';

import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {RunnerComponent} from "./runner.component";
import {RunnerRoutingModule} from "./runner-routing.module";
import {AngularSplitModule} from "angular-split-ng6";
import {ResultsComponent} from "./leftSide/results/results.component";
import {ResultDirectoryComponent} from "./leftSide/results/container/result-directory.component";
import {ResultFileComponent} from "./leftSide/results/container/leaf/result-file.component";
import {GenericModule} from "../../generic/generic.module";
import {DndModule} from "ng2-dnd";
import {ResultComponent} from "./main/result/result.component";
import {ResultResolver} from "./main/result/result.resolver";
import {FeaturesModule} from "../features/features.module";
import {RunnerResultTabsComponent} from "./main/runner-result-tabs.component";
import {TabViewModule} from "primeng/primeng";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        AngularSplitModule,
        DndModule.forRoot(),

        TabViewModule,

        GenericModule,
        RunnerRoutingModule,
        FeaturesModule,
    ],
    exports: [
        RunnerComponent,
    ],
    declarations: [
        RunnerComponent,
        ResultsComponent,
        ResultDirectoryComponent,
        ResultFileComponent,
        ResultComponent,
        RunnerResultTabsComponent,
    ],
    entryComponents: [
        ResultDirectoryComponent,
        ResultFileComponent,
    ],
    providers: [
        ResultResolver,
    ],
})
export class RunnerModule { }
