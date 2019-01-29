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
import {SettingsComponent} from "./functionalities/config/settings/settings.component";
import {SettingsService} from "./service/settings.service";
import {FileDirChooserInputComponent} from "./generic/components/form/file_dir_chooser/file-dir-chooser-input.component";
import {FileDirTreeContainerComponent} from "./generic/components/form/file_dir_chooser/file-dir-tree/nodes/container/file-dir-tree-container.component";
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
import {LicenseService} from "./functionalities/config/license/license.service";
import {LicenseComponent} from "./functionalities/config/license/license.component";
import {FileUploadModule, RadioButtonModule} from "primeng/primeng";
import {ContextService} from "./service/context.service";
import {MultiProjectHttpInterceptor} from "./service/interceptors/multi-prject.http-interceptor";
import {HomeModule} from "./functionalities/home/home.module";
import {HomeService} from "./service/home.service";
import {UtilService} from "./service/util.service";
import {ProjectService} from "./service/project.service";
import {ErrorsHandlerInterceptor} from "./service/interceptors/error-handler.interceptor";

@NgModule({
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        FormsModule,
        ModalModule.forRoot(),

        RadioButtonModule,
        FileUploadModule,

        HomeModule,
        FeaturesModule,
        ResultsModule,
        StepsModule,
        ResourcesModule,
        ManualModule,
        GenericModule,

        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        MenuComponent,
        PageNotFoundComponent,
        VariablesComponent,
        LicenseComponent,
        SettingsComponent,
        ArgValueValidatorDirective
    ],
    exports: [
        VariablesComponent,
    ],
    providers: [
        LicenseGuard,
        UnsavedChangesGuard,

        ApplicationEventBus,

        StepsService,
        TestsService,
        VariablesService,
        SettingsService,
        HomeService,
        ProjectService,
        FileSystemService,
        ResultService,
        FeatureService,
        TagsService,

        ResourceService,
        RdbmsService,
        HttpService,

        UrlService,
        UtilService,

        ErrorHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useExisting: ErrorHttpInterceptor,  multi: true },

        MultiProjectHttpInterceptor,
        { provide: HTTP_INTERCEPTORS, useClass: MultiProjectHttpInterceptor, multi: true },

        ErrorsHandlerInterceptor,
        { provide: ErrorHandler, useClass: ErrorsHandlerInterceptor},

        ContextService,
        {provide: APP_INITIALIZER, useFactory: initApplicationContext, deps: [ContextService], multi: true},

        MessageService,

        LicenseService,
    ],
    entryComponents: [
        FileDirChooserInputComponent,
        FileDirTreeContainerComponent,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}


export function initApplicationContext(contextService: ContextService){
    // Do initing of services that is required before app loads
    // NOTE: this factory needs to return a function (that then returns a promise)
    return () => contextService.init();
}
