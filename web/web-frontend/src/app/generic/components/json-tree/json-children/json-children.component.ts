import {Component, Input} from '@angular/core';
import {JsonTreeNodeState} from "../model/json-tree-node-state.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {TreeState} from "../model/state/TreeState";

@ Component({
    moduleId: module.id,
    selector: 'json-children',
    templateUrl: 'json-children.component.html',
    styleUrls:["json-children.component.scss"]
})

export class JsonChildrenComponent {

    @Input() model:JsonTreeContainer;
    @Input() treeState:TreeState;
    @Input() jsonTreeNodeState:JsonTreeNodeState;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasChildren() {
        return this.model.getChildren().length != 0;
    }

}
