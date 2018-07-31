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
        messageService.init();

        AppComponent.rootViewContainerRef = viewContainerRef;
        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
