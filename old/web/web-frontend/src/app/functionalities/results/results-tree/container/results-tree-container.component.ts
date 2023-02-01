import {Component, Input, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {ResultsTreeContainerModel} from "../model/results-tree-container.model";

@Component({
    selector: 'results-tree-container',
    templateUrl: 'results-tree-container.component.html',
    styleUrls: [
        'results-tree-container.component.scss',
        '../../../../generic/css/tree.scss'
    ]
})

export class ResultsTreeContainerComponent {

    @Input() model: ResultsTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
