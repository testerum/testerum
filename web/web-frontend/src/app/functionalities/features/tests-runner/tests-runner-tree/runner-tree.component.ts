import {Component, Input} from '@angular/core';

import {RunnerTreeNodeModel} from "./model/runner-tree-node.model";
import {TestsRunnerService} from "../tests-runner.service";

@Component({
    moduleId: module.id,
    selector: 'runner-tree',
    templateUrl: 'runner-tree.component.html',
    styleUrls:['runner-tree.component.scss']
})
export class RunnerTreeComponent {

    @Input() treeModel:RunnerTreeNodeModel;

    constructor(private testsRunnerService: TestsRunnerService) {}

    stopTests() {
        this.testsRunnerService.stopExecution();
    }

}
