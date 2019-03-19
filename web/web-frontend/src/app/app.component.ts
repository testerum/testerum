import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';
import {MessageService} from "./service/message.service";
import {setTheme} from "ngx-bootstrap";

@Component({
  moduleId:module.id,
  selector: 'my-app',
  templateUrl: './app.component.html'
})
export class AppComponent  {

    static rootViewContainerRef: ViewContainerRef;
    static componentFactoryResolver: ComponentFactoryResolver;
    constructor(messageService: MessageService,
                private viewContainerRef: ViewContainerRef,
                private componentFactoryResolver: ComponentFactoryResolver) {
        setTheme('bs3');

        AppComponent.rootViewContainerRef = viewContainerRef;

        messageService.init();
        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
