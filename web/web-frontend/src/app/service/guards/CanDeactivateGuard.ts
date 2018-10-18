import {ComponentCanDeactivate} from "../../generic/interfaces/can-deactivate/ComponentCanDeactivate";
import {CanDeactivate} from "@angular/router";
import {Injectable} from "@angular/core";
import {AreYouSureModalService} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {Warning} from "../../model/warning/Warning";
import {AreYouSureModalEnum} from "../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable()
export class CanDeactivateGuard implements CanDeactivate<ComponentCanDeactivate> {

    constructor(private areYouSureModalService: AreYouSureModalService) {
    }

    canDeactivate(component: ComponentCanDeactivate): Observable<boolean> | Promise<boolean> | boolean {

        if(!component.canDeactivate()){
            return this.areYouSureModalService.showAreYouSureModal(
                "Warning",
                "You have unsaved changes! If you leave, your changes will be lost."
            ).pipe(map((val: AreYouSureModalEnum) => val == AreYouSureModalEnum.OK));
        }
        return true;
    }
}
