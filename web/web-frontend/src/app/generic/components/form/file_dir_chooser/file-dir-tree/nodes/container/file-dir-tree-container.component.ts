import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {JsonTreeService} from "../../../../../json-tree/json-tree.service";
import {FileDirTreeContainerModel} from "../../model/file-dir-tree-container.model";
import {ModelComponentMapping} from "../../../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeNodeEventModel} from "../../../../../json-tree/event/selected-json-tree-node-event.model";
import {FileDirTreeComponentService} from "../../file-dir-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'file-directory-chooser-container',
    templateUrl: 'file-dir-tree-container.component.html',
    styleUrls: [
        'file-dir-tree-container.component.scss',
        '../../../../../../../generic/css/tree.scss'
    ]
})
export class FileDirTreeContainerComponent {

    @Input() model: FileDirTreeContainerModel;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    hasMouseOver: boolean = false;

    constructor(private jsonTreeService: JsonTreeService,
                private fileDirTreeComponentService: FileDirTreeComponentService) {
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren;
        if (this.model.jsonTreeNodeState.showChildren) {
            this.jsonTreeService.triggerExpendedNodeEvent(this.model)
        }
    }

    setSelected() {
        if (this.fileDirTreeComponentService.isTesterumProjectChooser && !this.model.isProject) {
            this.fileDirTreeComponentService.selectedNode = null;
            return;
        }

        this.fileDirTreeComponentService.selectedNode = this.model;
    }

    isSelected(): boolean {
        return this.model == this.fileDirTreeComponentService.selectedNode;
    }

    isTesterumProjectChooser(): boolean {
        return this.fileDirTreeComponentService.isTesterumProjectChooser;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }
}
