import {APP_INITIALIZER, ErrorHandler, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {MenuComponent} from "./menu/menu.component";
import {PageNotFoundComponent} from "./generic/components/page_not_found/page-not-found.component";
import {AppRoutingModule} from "./app-routing.module";
import {StepsModule} from "./functionalities/steps/steps.module";
import {GenericModule} from "./generic/generic.module";
import {CommonModule} from "@angular/common";
import {StepsService} from "./service/steps.service";
import {FormsModule} from "@angular/forms";
import {FeaturesModule} from "./functionalities/features/features.module";
import {TestsService} from "./service/tests.service";
import {ResourcesModule} from "./functionalities/resources/resources.module";
import {RdbmsService} from "./service/resources/rdbms/rdbms.service";
import {ResourceService} from "./service/resources/resource.service";
import {HttpService} from "./service/resources/http/http.service";
import {ErrorHttpInterceptor} from "./service/interceptors/error.http-interceptor";
import {ApplicationEventBus} from "./event-bus/application.eventbus";
import {VariablesComponent} from "./functionalities/variables/variables.component";
import {ModalModule} from "ngx-bootstrap/modal";
import {VariablesService} from "./service/variables.service";
import {FileSystemService} from "./service/file-system.service";
import {LicenseGuard} from "./service/guards/license.guard";
import {SettingsService} from "./service/settings.service";
import {FileChooserInputComponent} from "./generic/components/form/file_chooser/file-chooser-input.component";
import {FileTreeContainerComponent} from "./generic/components/form/file_chooser/file-tree/nodes/container/file-tree-container.component";
import {ResultsModule} from "./functionalities/results/results.module";
import {ResultService} from "./service/report/result.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ArgValueValidatorDirective} from "./generic/components/step-call-tree/arg-modal/validator/arg-value-validator.directive";
import {FeatureService} from "./service/feature.service";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {UrlService} from "./service/url.service";
import {TagsService} from "./service/tags.service";
import {MessageService} from "./service/message.service";
import {ManualModule} from "./functionalities/manual/manual.module";
import {UnsavedChangesGuard} from "./service/guards/unsaved-changes.guard";
import {DropdownModule, FileUploadModule, RadioButtonModule, TooltipModule} from "primeng/primeng";
import {ContextService} from "./service/context.service";
import {MultiProjectHttpInterceptor} from "./service/interceptors/multi-prject.http-interceptor";
import {HomeModule} from "./functionalities/home/home.module";
import {HomeService} from "./service/home.service";
import {UtilService} from "./service/util.service";
import {ProjectService} from "./service/project.service";
import {ErrorsHandlerInterceptor} from "./service/interceptors/error-handler.interceptor";
import {CurrentProjectGuard} from "./service/guards/current-project.guard";
import {ModelRepairerService} from "./service/model-repairer/model-repairer.service";
import {MenuVariablesComponent} from './menu/variables/menu-variables.component';
import {EnvironmentEditModalComponent} from './functionalities/variables/environment-edit-modal/environment-edit-modal.component';
import {NotFundComponent} from './functionalities/others/not-fund/not-fund.component';
import {NotFoundHttpInterceptor} from "./service/interceptors/not-found.http-interceptor";
import {ProjectReloadWsService} from "./service/project-reload-ws.service";
import {UserModule} from "./functionalities/user/user.module";
import {BsDropdownModule} from "ngx-bootstrap";
import {UserService} from "./service/user.service";
import {ProjectReloadModalService} from "./functionalities/others/project_reload_modal/project-reload-modal.service";
import {ProjectReloadModalComponent} from "./functionalities/others/project_reload_modal/project-reload-modal.component";
import {AuthenticationHttpInterceptor} from "./service/interceptors/authentication.http-interceptor";
import {ConfigModule} from "./functionalities/config/config.module";
import {RunConfigService} from "./service/run-config.service";
import { MenuRunnerComponent } from './menu/runner/menu-runner.component';
import {DemoService} from "./service/demo.service";

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        FormsModule,
        ModalModule.forRoot(),
        BsDropdownModule.forRoot(),

        RadioButtonModule,
        FileUploadModule,
        TooltipModule,
        DropdownModule,

        HomeModule,
        FeaturesModule,
        ResultsModule,
        StepsModule,
        ResourcesModule,
        ManualModule,
        GenericModule,
        UserModule,
        ConfigModule,

        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        MenuComponent,
        PageNotFoundComponent,
        VariablesComponent,
        ArgValueValidatorDirective,
        MenuVariablesComponent,
        EnvironmentEditModalComponent,
        NotFundComponent,
        ProjectReloadModalComponent,
        MenuRunnerComponent,
    ],
    exports: [
        VariablesComponent,
    ],
    providers: [
        LicenseGuard,
        UnsavedChangesGuard,
        CurrentProjectGuard,

        ApplicationEventBus,

        ProjectReloadModalService,

        StepsService,
        TestsService,
        VariablesService,
        SettingsService,
        HomeService,
        ProjectService,
        ProjectReloadWsService,
        FileSystemService,
        ResultService,
        FeatureService,
        TagsService,
        UserService,
        RunConfigService,
        DemoService,

        ResourceService,
        RdbmsService,
        HttpService,

        UrlService,
        UtilService,
        ModelRepairerService,


        ErrorHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useExisting: ErrorHttpInterceptor,  multi: true },

        NotFoundHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useExisting: NotFoundHttpInterceptor,  multi: true },

        MultiProjectHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useClass: MultiProjectHttpInterceptor, multi: true },

        AuthenticationHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useClass: AuthenticationHttpInterceptor, multi: true },

        ErrorsHandlerInterceptor,
        { provide: ErrorHandler, useClass: ErrorsHandlerInterceptor},

        ContextService,
        { provide: APP_INITIALIZER, useFactory:  (service: ContextService) => function() {return service.init()}, deps: [ContextService], multi: true },

        MessageService,
        { provide: APP_INITIALIZER, useFactory:  (service: MessageService) => function() {return service.init()}, deps: [MessageService], multi: true },
    ],
    entryComponents: [
        FileChooserInputComponent,
        FileTreeContainerComponent,
        ProjectReloadModalComponent,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
