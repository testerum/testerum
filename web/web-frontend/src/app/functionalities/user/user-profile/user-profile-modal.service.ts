import {ComponentFactoryResolver, Injectable} from "@angular/core";
import {AppComponent} from "../../../app.component";
import {UserProfileComponent} from "./user-profile.component";

@Injectable()
export class UserProfileModalService {

    constructor(private componentFactoryResolver: ComponentFactoryResolver) {
    }

    showUserProfileModal() {
        const factory = this.componentFactoryResolver.resolveComponentFactory(UserProfileComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        let modalInstance: UserProfileComponent = modalComponentRef.instance;

        modalInstance.modalComponentRef = modalComponentRef;
    }
}
