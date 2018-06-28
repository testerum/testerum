import { NgModule } from '@angular/core';

import { StepsComponent } from './steps.component';
import {StepsRoutingModule} from "./steps-routing.module";
import {GenericModule} from "../../generic/generic.module";
import {BrowserModule} from "@angular/platform-browser";
import {StepsService} from "../../service/steps.service";
import {StepsTreeService} from "./steps-tree/steps-tree.service";
import {FormsModule} from "@angular/forms";
import {DndModule} from "ng2-dnd";
import {ComposedStepEditorComponent} from "./composed-step-editor/composed-step-editor.component";
import {ComposedStepEditorResolver} from "./composed-step-editor/composed-step-editor.resolver";
import {AngularSplitModule} from "angular-split";
import {ResourcesModule} from "../resources/resources.module";
import {JsonStepContainerComponent} from "./steps-tree/container/json-step-container.component";
import {JsonStepNodeComponent} from "./steps-tree/container/node/json-step-node.component";
import {UpdateIncompatibilityDialogComponent} from "./composed-step-editor/update-incompatilibity-dialog/update-incompatibility-dialog.component";
import {BasicStepEditorComponent} from "./basic-step-editor/basic-step-editor.component";
import {BasicStepEditorResolver} from "./basic-step-editor/basic-step-editor.resolver";
import {BasicStepParametersComponent} from "./basic-step-editor/basic-step-parameters/basic-step-parameters.component";
import {CollapseModule, ModalModule, PopoverModule, SortableModule} from "ngx-bootstrap";
import {ComposedStepParametersComponent} from "./composed-step-editor/composed-step-parameters/composed-step-parameters.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        ModalModule.forRoot(),
        SortableModule.forRoot(),
        CollapseModule.forRoot(),
        DndModule.forRoot(),
        PopoverModule.forRoot(),
        AngularSplitModule,

        StepsRoutingModule,
        GenericModule,
        ResourcesModule,
    ],
    exports: [
        JsonStepContainerComponent,
        JsonStepNodeComponent,

        StepsComponent,
        ComposedStepEditorComponent,
        UpdateIncompatibilityDialogComponent,
    ],
    declarations: [
        JsonStepContainerComponent,
        JsonStepNodeComponent,

        StepsComponent,

        ComposedStepEditorComponent,
        UpdateIncompatibilityDialogComponent,
        ComposedStepParametersComponent,

        BasicStepEditorComponent,
        BasicStepParametersComponent,
    ],
    entryComponents: [
        JsonStepContainerComponent,
        JsonStepNodeComponent,
    ],
    providers: [
        StepsService,
        StepsTreeService,

        ComposedStepEditorResolver,
        BasicStepEditorResolver,
    ],
})
export class StepsModule { }
