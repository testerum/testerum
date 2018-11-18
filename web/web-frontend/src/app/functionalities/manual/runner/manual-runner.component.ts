import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ManualTestPlansService} from "../service/manual-test-plans.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestPlan} from "../plans/model/manual-test-plan.model";
import {AbstractComponentCanDeactivate} from "../../../generic/interfaces/can-deactivate/AbstractComponentCanDeactivate";
import {ManualRunnerEditorComponent} from "./editor/manual-runner-editor.component";

@Component({
    selector: 'manual-tests-executor',
    templateUrl: 'manual-runner.component.html',
    styleUrls: ['manual-runner.component.scss']
})
export class ManualRunnerComponent extends AbstractComponentCanDeactivate implements OnInit {

    @ViewChild(ManualRunnerEditorComponent) manualRunnerEditorComponent: ManualRunnerEditorComponent;

    planPath: Path;
    manualExecPlan: ManualTestPlan;

    constructor(private route: ActivatedRoute,
                private manualExecPlansService: ManualTestPlansService) {
        super();
    }

    ngOnInit() {
        let runnerPlanPathAsString = this.route.snapshot.params["planPath"];
        this.planPath = Path.createInstance(runnerPlanPathAsString);

        this.manualExecPlansService.getManualExecPlan(Path.createInstance(runnerPlanPathAsString)).subscribe( (manualExecPlan: ManualTestPlan) => {
            this.manualExecPlan = manualExecPlan;
        });
    }

    canDeactivate(): boolean {
        return !this.manualRunnerEditorComponent.hasStateChanged;
    }
}
