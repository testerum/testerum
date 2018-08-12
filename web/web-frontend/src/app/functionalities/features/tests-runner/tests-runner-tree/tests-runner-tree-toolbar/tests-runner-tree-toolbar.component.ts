import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TestsRunnerService} from "../../tests-runner.service";
import {RunnerTreeFilterModel} from "../model/filter/runner-tree-filter.model";

@Component({
    selector: 'tests-runner-tree-toolbar',
    templateUrl: './tests-runner-tree-toolbar.component.html',
    styleUrls: ['./tests-runner-tree-toolbar.component.scss']
})
export class TestsRunnerTreeToolbarComponent implements OnInit {

    @Output() stopTests: EventEmitter<string> = new EventEmitter<string>();

    model = new RunnerTreeFilterModel();

    constructor(private testRunnerService: TestsRunnerService) {}

    ngOnInit() {}

    onRunOrStopTests() {
        if (this.areTestRunning()) {
            this.stopTests.emit(null);
        } else {
            this.testRunnerService.reRunTests()
        }
    }

    areTestRunning(): boolean {
        return this.testRunnerService.areTestRunning;
    }

    onToggleFolders() {
        this.model.areTestFoldersShown = !this.model.areTestFoldersShown;
        this.testRunnerService.showTestFoldersEventObservable.emit(this.model.areTestFoldersShown);
    }

    onToggleWaiting() {
        this.model.showWaiting = !this.model.showWaiting;
        this.triggerFilterChangeEvent();
    }

    onTogglePassed() {
        this.model.showPassed = !this.model.showPassed;
        this.triggerFilterChangeEvent();
    }

    onToggleActive() {
        this.model.showFailed = !this.model.showFailed;
        this.triggerFilterChangeEvent();
    }

    onToggleError() {
        this.model.showError = !this.model.showError;
        this.triggerFilterChangeEvent();
    }

    onToggleUndefined() {
        this.model.showUndefined = !this.model.showUndefined;
        this.triggerFilterChangeEvent();
    }

    onToggleSkipped() {
        this.model.showSkipped = !this.model.showSkipped;
        this.triggerFilterChangeEvent();
    }

    private triggerFilterChangeEvent() {
        this.testRunnerService.treeFilterObservable.emit(this.model)
    }
}
