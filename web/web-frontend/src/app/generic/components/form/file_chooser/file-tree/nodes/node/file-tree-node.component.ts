import {Component, Input} from '@angular/core';
import {FileTreeNode} from "../../model/file-tree-node.model";
import {ModelComponentMapping} from "../../../../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeNodeState} from "../../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeService} from "../../../../../json-tree/json-tree.service";
import {FileTreeComponentService} from "../../file-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'file-tree-node',
    templateUrl: 'file-tree-node.component.html',
    styleUrls:['file-tree-node.component.scss']
})
export class FileTreeNodeComponent {

    @Input() model:FileTreeNode;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;

    hasMouseOver: boolean = false;

    constructor(private jsonTreeService: JsonTreeService,
                private fileTreeComponentService: FileTreeComponentService) {
    }

    setSelected() {
        if (this.fileTreeComponentService.isTesterumProjectChooser) {
            this.fileTreeComponentService.selectedNode = null;
            return;
        }
        this.fileTreeComponentService.selectedNode = this.model;
    }

    isSelected(): boolean {
        return this.model == this.fileTreeComponentService.selectedNode;
    }
}
