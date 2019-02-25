import {Component, ComponentFactoryResolver, ViewContainerRef} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {

    static rootViewContainerRef: ViewContainerRef;
    static componentFactoryResolver: ComponentFactoryResolver;

    constructor(private viewContainerRef: ViewContainerRef,
                private componentFactoryResolver: ComponentFactoryResolver) {

        AppComponent.rootViewContainerRef = viewContainerRef;
        AppComponent.componentFactoryResolver = componentFactoryResolver
    }
}
