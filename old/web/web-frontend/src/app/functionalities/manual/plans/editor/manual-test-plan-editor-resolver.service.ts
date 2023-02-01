import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {ManualTestPlan} from "../model/manual-test-plan.model";

@Injectable()
export class ManualTestPlanEditorResolver implements Resolve<any> {

    constructor(private manualExecPlansService: ManualTestPlansService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['planPath'];
        let path = pathAsString ? Path.createInstance(pathAsString) : Path.createInstanceOfEmptyPath();

        if (action == "create") {
            let manualExecPlan = new ManualTestPlan();
            manualExecPlan.path = path;

            return manualExecPlan;
        }

        return this.manualExecPlansService.getManualExecPlan(path);
    }
}
