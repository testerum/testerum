import {NgModule}      from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent}  from './app.component';
import {MenuComponent} from "./menu/menu.component";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {AppRoutingModule} from "./app-routing.module";
import {StepsModule} from "./functionalities/steps/steps.module";
import {GenericModule} from "./generic/generic.module";
import {CommonModule} from "@angular/common";
import { HttpModule, JsonpModule } from '@angular/http';
import {StepsService} from "./service/steps.service";
import {FormsModule} from "@angular/forms";
import {FeaturesModule} from "./functionalities/features/features.module";
import {TestsService} from "./service/tests.service";
import {TestWebSocketService} from "./service/test-web-socket.service";
import {ResourcesModule} from "./functionalities/resources/resources.module";
import {RdbmsService} from "./service/resources/rdbms/rdbms.service";
import {ResourceService} from "./service/resources/resource.service";
import {HttpService} from "./service/resources/http/http.service";
import {ConfigService} from "./service/config.service";
import {ErrorService} from "./service/error.service";
import {ApplicationEventBus} from "./event-bus/application.eventbus";
import {VariablesComponent} from "./functionalities/variables/variables.component";
import {ModalModule} from "ngx-bootstrap/modal";
import {VariablesService} from "./service/variables.service";
import {SetupComponent} from "./functionalities/config/setup/setup.component";
import {SetupService} from "./service/setup.service";
import {FileSystemService} from "./service/file-system.service";
import {SetupGuard} from "./service/guards/setup.guard";
import {SettingsComponent} from "./functionalities/config/settings/settings.component";
import {SettingsService} from "./service/settings.service";
import {FileDirChooserComponent} from "./generic/components/form/file_dir_chooser/file-dir-chooser.component";
import {FileDirectoryChooserContainerComponent} from "./generic/components/form/file_dir_chooser/container/file-directory-chooser-container.component";
import {RunnerModule} from "./functionalities/runner/runner.module";
import {ResultService} from "./service/report/result.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ManualTestsModule} from "./manual-tests/manual-tests.module";
import {ArgValueValidatorDirective} from "./generic/components/step-call-tree/arg-modal/validator/arg-value-validator.directive";
import {FeatureService} from "./service/feature.service";
import {HttpClientModule} from "@angular/common/http";
import {AttachmentsService} from "./service/attachments.service";

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpModule,
        HttpClientModule,
        FormsModule,
        JsonpModule,
        ModalModule.forRoot(),

        FeaturesModule,
        RunnerModule,
        StepsModule,
        ResourcesModule,
        GenericModule,

        ManualTestsModule,

        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        MenuComponent,
        PageNotFoundComponent,
        VariablesComponent,
        SetupComponent,
        SettingsComponent,
        ArgValueValidatorDirective,
    ],
    exports: [
        VariablesComponent,
    ],
    providers: [
        SetupGuard,

        ApplicationEventBus,
        ConfigService,
        ErrorService,

        TestWebSocketService,
        StepsService,
        TestsService,
        VariablesService,
        SetupService,
        SettingsService,
        FileSystemService,
        ResultService,
        FeatureService,
        AttachmentsService,

        ResourceService,
        RdbmsService,
        HttpService,
    ],
    entryComponents: [
        FileDirChooserComponent,
        FileDirectoryChooserContainerComponent,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
