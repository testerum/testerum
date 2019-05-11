import {NgModule} from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {GenericModule} from "../../generic/generic.module";
import {ModalModule} from "ngx-bootstrap";
import {ConfigRoutingModule} from "./config-routing.module";
import {SettingsModalComponent} from "./settings/settings-modal.component";
import {SettingsModalService} from "./settings/settings-modal.service";
import {ListboxModule} from "primeng/listbox";
import {
    AutoCompleteModule,
    KeyFilterModule,
    OrderListModule,
    TabViewModule,
    ToolbarModule,
    TooltipModule
} from "primeng/primeng";
import { RunnerModalComponent } from './runner/runner-modal.component';
import {RunnerModalService} from "./runner/runner-modal.service";
import {AngularSplitModule} from "angular-split";
import { RunnersConfigListComponent } from './runner/list/runners-config-list.component';
import {RunnerConfigService} from "./runner/runner-config.service";
import { RunnersConfigToobarComponent } from './runner/list/runners-config-toobar/runners-config-toobar.component';
import { RunnerConfigEditorComponent } from './runner/editor/runner-config-editor.component';
import { RunnerConfigTestsToExecuteComponent } from './runner/editor/runner-config-tests-to-execute/runner-config-tests-to-execute.component';

@NgModule({
    imports: [
        ConfigRoutingModule,

        BrowserModule,
        FormsModule,

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
    exports: [
    ],
    declarations: [
        SettingsModalComponent,
        RunnerModalComponent,
        RunnersConfigListComponent,
        RunnersConfigToobarComponent,
        RunnerConfigEditorComponent,
        RunnerConfigTestsToExecuteComponent,
    ],
    entryComponents: [
        SettingsModalComponent,
        RunnerModalComponent,
    ],
    providers: [
        SettingsModalService,
        RunnerModalService,
        RunnerConfigService
    ],
})
export class ConfigModule { }
