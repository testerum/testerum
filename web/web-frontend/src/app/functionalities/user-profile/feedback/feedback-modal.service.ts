import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {FeedbackComponent} from "./feedback.component";

@Injectable()
export class FeedbackModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showFeedbackModal() {
        const factory = this.componentFactoryResolver.resolveComponentFactory(FeedbackComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: FeedbackComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
    }
}
