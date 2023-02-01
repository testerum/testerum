import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {JsonTreeService} from "../../../../../json-tree/json-tree.service";
import {FileTreeContainer} from "../../model/file-tree.container";
import {ModelComponentMapping} from "../../../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeNodeEventModel} from "../../../../../json-tree/event/selected-json-tree-node-event.model";
import {FileTreeComponentService} from "../../file-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'file-directory-chooser-container',
    templateUrl: 'file-tree-container.component.html',
    styleUrls: [
        'file-tree-container.component.scss',
        '../../../../../../../generic/css/tree.scss'
    ]
})
export class FileTreeContainerComponent {

    @Input() model: FileTreeContainer;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    hasMouseOver: boolean = false;

    constructor(private jsonTreeService: JsonTreeService,
                private fileDirTreeComponentService: FileTreeComponentService) {
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
