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

    path: Path;
    manualExecPlan: ManualExecPlan;

    constructor(private route: ActivatedRoute,
                private manualExecPlansService: ManualExecPlansService) {
    }

    ngOnInit() {
        let runnerPathAsString = this.route.snapshot.params["path"];
        this.path = Path.createInstance(runnerPathAsString);

        this.manualExecPlansService.getManualExecPlan(Path.createInstance(runnerPathAsString)).subscribe( (manualExecPlan: ManualExecPlan) => {
            this.manualExecPlan = manualExecPlan;
        });
    }
}
