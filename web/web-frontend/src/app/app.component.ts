import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';
import {setTheme} from "ngx-bootstrap";
import {ProjectReloadWsService} from "./service/project-reload-ws.service";
import {ProjectReloadModalService} from "./functionalities/others/project_reload_modal/project-reload-modal.service";
import {LicenseAlertModalService} from "./functionalities/user/license/alert/license-alert-modal.service";

@Component({
  moduleId:module.id,
  selector: 'my-app',
  templateUrl: './app.component.html'
})
export class AppComponent  {

    static rootViewContainerRef: ViewContainerRef;
    static componentFactoryResolver: ComponentFactoryResolver;
    constructor(projectReloadWsService: ProjectReloadWsService,
                private viewContainerRef: ViewContainerRef,
                private componentFactoryResolver: ComponentFactoryResolver,
                private projectReloadModalService: ProjectReloadModalService,
                private licenseAlertModalService: LicenseAlertModalService) {
        setTheme('bs3');

        AppComponent.rootViewContainerRef = viewContainerRef;

        this.licenseAlertModalService.onApplicationInitialize();

        projectReloadWsService.projectReloadedEventEmitter.subscribe((projectRootDir: string) => {
            projectReloadModalService.showProjectReloadModal(projectRootDir);
        });
        projectReloadWsService.start();

        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
