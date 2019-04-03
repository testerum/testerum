import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';
import {MessageService} from "./service/message.service";
import {setTheme} from "ngx-bootstrap";
import {ProjectReloadWsService} from "./service/project-reload-ws.service";
import {ProjectReloadModalService} from "./functionalities/others/project_reload_modal/project-reload-modal.service";
import {Path} from "./model/infrastructure/path/path.model";

@Component({
  moduleId:module.id,
  selector: 'my-app',
  templateUrl: './app.component.html'
})
export class AppComponent  {

    static rootViewContainerRef: ViewContainerRef;
    static componentFactoryResolver: ComponentFactoryResolver;
    constructor(messageService: MessageService,
                projectReloadWsService: ProjectReloadWsService,
                private viewContainerRef: ViewContainerRef,
                private componentFactoryResolver: ComponentFactoryResolver,
                private projectReloadModalService: ProjectReloadModalService) {
        setTheme('bs3');

        AppComponent.rootViewContainerRef = viewContainerRef;

        messageService.init();

        projectReloadWsService.projectReloadedEventEmitter.subscribe((projectRootDir: string) => {
            projectReloadModalService.showProjectReloadModal(projectRootDir);
        });
        projectReloadWsService.start();

        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
