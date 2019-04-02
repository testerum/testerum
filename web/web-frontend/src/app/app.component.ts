import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';
import {MessageService} from "./service/message.service";
import {setTheme} from "ngx-bootstrap";
import {ProjectReloadWsService} from "./service/project-reload-ws.service";

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
                private componentFactoryResolver: ComponentFactoryResolver) {
        setTheme('bs3');

        AppComponent.rootViewContainerRef = viewContainerRef;

        messageService.init();

        projectReloadWsService.projectReloadedEventEmitter.subscribe((projectRootDir: string) => {
            console.log(`project at path [${projectRootDir}] was reloaded`);
        });
        projectReloadWsService.start();

        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
