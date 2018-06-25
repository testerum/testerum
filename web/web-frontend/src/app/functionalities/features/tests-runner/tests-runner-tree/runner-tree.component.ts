import {Component, Input, OnInit} from '@angular/core';

import {RunnerTreeNodeModel} from "./model/runner-tree-node.model";
import {ExecutionPieModel} from "../../../../generic/components/charts/execution-pie/model/execution-pie.model";

@Component({
    moduleId: module.id,
    selector: 'runner-tree',
    templateUrl: 'runner-tree.component.html',
    styleUrls:['runner-tree.component.css']
})
export class RunnerTreeComponent {

    @Input() treeModel:RunnerTreeNodeModel;
    @Input() pieModel: ExecutionPieModel;

}
