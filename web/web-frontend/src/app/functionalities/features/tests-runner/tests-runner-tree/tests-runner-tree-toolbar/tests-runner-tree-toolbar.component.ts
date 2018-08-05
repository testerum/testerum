import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
    selector: 'tests-runner-tree-toolbar',
    templateUrl: './tests-runner-tree-toolbar.component.html',
    styleUrls: ['./tests-runner-tree-toolbar.component.scss']
})
export class TestsRunnerTreeToolbarComponent implements OnInit {

    @Output() stopTests: EventEmitter<string> = new EventEmitter<string>();

    constructor() {}

    ngOnInit() {}

    onStopTests() {
        this.stopTests.emit(null);
    }

}
