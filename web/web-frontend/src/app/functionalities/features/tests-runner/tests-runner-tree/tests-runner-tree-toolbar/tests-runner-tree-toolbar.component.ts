import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TestsRunnerService} from "../../tests-runner.service";

@Component({
    selector: 'tests-runner-tree-toolbar',
    templateUrl: './tests-runner-tree-toolbar.component.html',
    styleUrls: ['./tests-runner-tree-toolbar.component.scss']
})
export class TestsRunnerTreeToolbarComponent implements OnInit {

    @Output() stopTests: EventEmitter<string> = new EventEmitter<string>();

    areTestFoldersShown = true;

    constructor(private testRunnerService: TestsRunnerService) {}

    ngOnInit() {}

    onRunOrStopTests() {
        if (this.areTestRunning()) {
            this.stopTests.emit(null);
        } else {
            this.testRunnerService.reRunTests()
        }
    }

    onToggleFolders() {
        this.areTestFoldersShown = !this.areTestFoldersShown;
        this.testRunnerService.showTestFoldersEventObservable.emit(this.areTestFoldersShown);
    }

    areTestRunning(): boolean {
        return this.testRunnerService.areTestRunning;
    }
}
