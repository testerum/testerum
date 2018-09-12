import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualExecPlansService} from "../../service/manual-exec-plans.service";
import {ManualExecPlan} from "../model/manual-exec-plan.model";

@Injectable()
export class ManualExecPlanEditorResolver implements Resolve<any> {

    constructor(private manualExecPlansService: ManualExecPlansService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : Path.createInstanceOfEmptyPath();

        if (action == "create") {
            let manualExecPlan = new ManualExecPlan();
            manualExecPlan.path = path;

            return manualExecPlan;
        }

        return this.manualExecPlansService.getManualExecPlan(path);
    }
}
