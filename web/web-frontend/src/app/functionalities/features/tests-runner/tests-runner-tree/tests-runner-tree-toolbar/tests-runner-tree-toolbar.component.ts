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

    onStopTests() {
        this.stopTests.emit(null);
    }

    onToggleFolders() {
        this.areTestFoldersShown = !this.areTestFoldersShown;
        this.testRunnerService.showTestFoldersEventObservable.emit(this.areTestFoldersShown);
    }
}
