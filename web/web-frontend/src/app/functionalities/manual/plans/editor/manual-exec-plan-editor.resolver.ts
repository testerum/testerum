import {Component, Injectable, OnInit} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Params, Resolve} from "@angular/router";
import {ManualTestsRunnerService} from "../../../../manual-tests/runner/service/manual-tests-runner.service";
import {switchMap} from "rxjs/operators";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualTestsRunner} from "../../../../manual-tests/runner/model/manual-tests-runner.model";
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
