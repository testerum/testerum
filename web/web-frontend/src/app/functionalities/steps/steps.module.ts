import {NgModule} from '@angular/core';
import {StepsComponent} from './steps.component';
import {StepsRoutingModule} from "./steps-routing.module";
import {GenericModule} from "../../generic/generic.module";
import {BrowserModule} from "@angular/platform-browser";
import {StepsService} from "../../service/steps.service";
import {StepsTreeService} from "./steps-tree/steps-tree.service";
import {FormsModule} from "@angular/forms";
import {DndModule} from "ng2-dnd";
import {ComposedStepEditorComponent} from "./composed-step-editor/composed-step-editor.component";
import {ComposedStepEditorResolver} from "./composed-step-editor/composed-step-editor.resolver";
import {ResourcesModule} from "../resources/resources.module";
import {JsonStepContainerComponent} from "./steps-tree/container/json-step-container.component";
import {JsonStepNodeComponent} from "./steps-tree/container/node/json-step-node.component";
import {BasicStepEditorComponent} from "./basic-step-editor/basic-step-editor.component";
import {BasicStepEditorResolver} from "./basic-step-editor/basic-step-editor.resolver";
import {BasicStepParametersComponent} from "./basic-step-editor/basic-step-parameters/basic-step-parameters.component";
import {CollapseModule, ModalModule, PopoverModule, SortableModule} from "ngx-bootstrap";
import {StepsTreeComponent} from "./steps-tree/steps-tree.component";
import {StepsTreeFilterComponent} from "./steps-tree/steps-tree-filter/steps-tree-filter.component";
import {ComposedStepViewComponent} from "../../generic/components/step/composed-step-view/composed-step-view.component";
import {AngularSplitModule} from "angular-split";
import {ToolbarModule} from "primeng/toolbar";
import {TooltipModule} from "primeng/tooltip";
import {SelectButtonModule} from "primeng/selectbutton";
import {AutoCompleteModule} from "primeng/autocomplete";
import {InputTextModule} from "primeng/inputtext";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ModalModule.forRoot(),
        SortableModule.forRoot(),
        CollapseModule.forRoot(),
        DndModule.forRoot(),
        PopoverModule.forRoot(),
        AngularSplitModule.forRoot(),

        TooltipModule,
        ToolbarModule,
        SelectButtonModule,
        AutoCompleteModule,
        InputTextModule,

        StepsRoutingModule,
        GenericModule,
        ResourcesModule,
    ],
    exports: [
        JsonStepContainerComponent,
        JsonStepNodeComponent,

        StepsComponent,
        ComposedStepEditorComponent,
    ],
    declarations: [
        StepsTreeFilterComponent,

        StepsTreeComponent,
        JsonStepContainerComponent,
        JsonStepNodeComponent,

        StepsComponent,

        ComposedStepEditorComponent,

        BasicStepEditorComponent,
        BasicStepParametersComponent,
    ],
    entryComponents: [
        JsonStepContainerComponent,
        JsonStepNodeComponent,
        ComposedStepViewComponent,
    ],
    providers: [
        StepsService,
        StepsTreeService,

        ComposedStepEditorResolver,
        BasicStepEditorResolver,
    ],
})
export class StepsModule { }
