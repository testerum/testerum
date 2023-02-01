import {Injectable} from "@angular/core";
import {AfterPageSaveListener} from "./listeners/after-page-save.listener";
import {ArrayUtil} from "../utils/array.util";

@Injectable()
export class ApplicationEventBus {

    private afterPageSaveListeners:Array<AfterPageSaveListener> = [];

    addAfterPageSaveListener(listener: AfterPageSaveListener): void {
        this.afterPageSaveListeners.push(listener)
    }

    removeAfterPageSaveListener(listener: AfterPageSaveListener): void {
        ArrayUtil.removeElementFromArray(this.afterPageSaveListeners, listener)
    }

    triggerAfterPageSaveEvent() {
        for (let listener of this.afterPageSaveListeners) {
            listener.afterPageSave();
        }
    }
}
