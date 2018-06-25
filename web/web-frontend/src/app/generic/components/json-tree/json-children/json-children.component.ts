import {Component, Input, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {JsonTreeNodeState} from "../model/json-tree-node-state.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";

@ Component({
    moduleId: module.id,
    selector: 'json-children',
    templateUrl: 'json-children.component.html',
    styleUrls:["json-children.component.css"]
})

export class JsonChildrenComponent {

    @Input() model:JsonTreeContainer;
    @Input() jsonTreeNodeState:JsonTreeNodeState;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasChildren() {
        return this.model.getChildren().length != 0;
    }

}
