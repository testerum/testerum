import {Directive, HostListener} from "@angular/core";

@Directive()
export abstract class AbstractComponentCanDeactivate {

    abstract canDeactivate(): boolean;

    @HostListener('window:beforeunload', ['$event'])
    unloadPageNotification($event: any) {
        if (!this.canDeactivate()) {
            $event.returnValue =true;
        }
    }
}
