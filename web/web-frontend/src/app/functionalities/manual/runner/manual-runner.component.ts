import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot} from "@angular/router";
import {ManualExecPlansService} from "../service/manual-exec-plans.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualExecPlan} from "../plans/model/manual-exec-plan.model";

@Component({
    selector: 'manual-tests-executor',
    templateUrl: 'manual-runner.component.html',
    styleUrls: ['manual-runner.component.scss']
})
export class ManualRunnerComponent implements OnInit {

    planPath: Path;
    manualExecPlan: ManualExecPlan;

    constructor(private route: ActivatedRoute,
                private manualExecPlansService: ManualExecPlansService) {
    }

    ngOnInit() {
        let runnerPlanPathAsString = this.route.snapshot.params["planPath"];
        this.planPath = Path.createInstance(runnerPlanPathAsString);

        this.manualExecPlansService.getManualExecPlan(Path.createInstance(runnerPlanPathAsString)).subscribe( (manualExecPlan: ManualExecPlan) => {
            this.manualExecPlan = manualExecPlan;
        });
    }
}
