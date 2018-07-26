import {Component, Input, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {ResultsDirectory} from "../model/results-directory.model";

@Component({
    selector: 'result-directory',
    templateUrl: 'result-directory.component.html',
    styleUrls: [
        'result-directory.component.scss',
        '../../../../../generic/components/json-tree/json-tree.generic.scss',
        '../../../../../generic/css/tree.scss'
    ]
})

export class ResultDirectoryComponent implements OnInit {

    @Input() model: ResultsDirectory;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    constructor() {
    }

    ngOnInit() {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
