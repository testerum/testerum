import {NgModule} from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {GenericModule} from "../../generic/generic.module";
import {ModalModule} from "ngx-bootstrap";
import {ConfigRoutingModule} from "./config-routing.module";
import {SettingsModalComponent} from "./settings/settings-modal.component";
import {SettingsModalService} from "./settings/settings-modal.service";
import {ListboxModule} from "primeng/listbox";
import {KeyFilterModule} from "primeng/primeng";

@NgModule({
    imports: [
        ConfigRoutingModule,

        BrowserModule,
        FormsModule,

        ModalModule.forRoot(),
        ListboxModule,

        GenericModule,
    ],
    exports: [
    ],
    declarations: [
        SettingsModalComponent,
    ],
    entryComponents: [
        SettingsModalComponent,
    ],
    providers: [
        SettingsModalService,
    ],
})
export class ConfigModule { }
