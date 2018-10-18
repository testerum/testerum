import {HostListener} from "@angular/core";

export abstract class AbstractComponentCanDeactivate {

    abstract canDeactivate(): boolean;

    @HostListener('window:beforeunload', ['$event'])
    unloadPageNotification($event: any) {
        if (!this.canDeactivate()) {
            $event.returnValue =true;
        }
    }
}
