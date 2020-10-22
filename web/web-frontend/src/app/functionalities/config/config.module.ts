import {NgModule} from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {GenericModule} from "../../generic/generic.module";
import {ModalModule} from "ngx-bootstrap";
import {ConfigRoutingModule} from "./config-routing.module";
import {SettingsModalComponent} from "./settings/settings-modal.component";
import {SettingsModalService} from "./settings/settings-modal.service";
import {ListboxModule} from "primeng/listbox";
import {AutoCompleteModule, OrderListModule, TabViewModule, ToolbarModule, TooltipModule} from "primeng/primeng";
import {RunConfigModalComponent} from './run-config/run-config-modal.component';
import {RunConfigModalService} from "./run-config/run-config-modal.service";
import {AngularSplitModule} from "angular-split";
import {RunnersConfigListComponent} from './run-config/list/runners-config-list.component';
import {RunConfigComponentService} from "./run-config/run-config-component.service";
import {RunnersConfigToobarComponent} from './run-config/list/runners-config-toobar/runners-config-toobar.component';
import {RunConfigEditorComponent} from './run-config/editor/run-config-editor.component';
import {RunConfigTestsToExecuteComponent} from './run-config/editor/run-config/run-config-tests-to-execute.component';
import {RunConfigTestTreeService} from "./run-config/editor/run-config/run-config-test-tree/run-config-test-tree.service";
import {RunConfigTestTreeComponent} from "./run-config/editor/run-config/run-config-test-tree/run-config-test-tree.component";
import {RunConfigTestTreeNodeComponent} from "./run-config/editor/run-config/run-config-test-tree/nodes/run-tree-node/run-config-test-tree-node.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {DynamicSettingComponentComponent} from './settings/dynamic-setting-component/dynamic-setting-component.component';

@NgModule({
    imports: [
        ConfigRoutingModule,
        BrowserModule,
        FormsModule,
        BrowserAnimationsModule,

        AngularSplitModule.forRoot(),
        ModalModule.forRoot(),
        ListboxModule,
        OrderListModule,
        ToolbarModule,
        TooltipModule,
        TabViewModule,
        AutoCompleteModule,

        GenericModule,
    ],
    exports: [],
    declarations: [
        SettingsModalComponent,

        RunConfigModalComponent,
        RunnersConfigListComponent,
        RunnersConfigToobarComponent,
        RunConfigEditorComponent,
        RunConfigTestsToExecuteComponent,
        RunConfigTestTreeComponent,
        RunConfigTestTreeNodeComponent,
        DynamicSettingComponentComponent,
    ],
    entryComponents: [
        SettingsModalComponent,
        RunConfigModalComponent,
        RunConfigTestTreeNodeComponent,
    ],
    providers: [
        SettingsModalService,
        RunConfigModalService,
        RunConfigComponentService,
        RunConfigTestTreeService,
    ],
})
export class ConfigModule {
}
