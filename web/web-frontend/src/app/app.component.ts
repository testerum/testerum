import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';
import {MessageService} from "./service/message.service";

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
        AppComponent.rootViewContainerRef = viewContainerRef;

        messageService.init();
        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
