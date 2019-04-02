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

    constructor(private route: ActivatedRoute) {
        super();
    }

    ngOnInit() {
        let planPathAsString = this.route.snapshot.params["planPath"];
        this.planPath = Path.createInstance(planPathAsString);
    }

    canDeactivate(): boolean {
        return !this.manualRunnerEditorComponent.isEditMode;
    }
}
