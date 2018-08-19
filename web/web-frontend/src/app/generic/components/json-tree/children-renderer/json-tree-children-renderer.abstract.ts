import {Input} from "@angular/core";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {JsonTreeNodeState} from "../model/json-tree-node-state.model";
import {JsonVerifyTreeService} from "../../json-verify/json-verify-tree/json-verify-tree.service";

export abstract class JsonTreeChildrenRenderer {
    @Input() model:JsonTreeContainer;
    @Input() modelComponentMapping: ModelComponentMapping;
    @Input() jsonTreeNodeState:JsonTreeNodeState = new JsonTreeNodeState;
    hasMouseOver: boolean = false;

    jsonVerifyTreeService:JsonVerifyTreeService;
    constructor(jsonVerifyTreeService:JsonVerifyTreeService) {
        this.jsonVerifyTreeService = jsonVerifyTreeService;
    }

    isCollapsed(): boolean {
        return this.model.hasChildren() && !this.model.getNodeState().showChildren
    }

    isEmpty(): boolean {
        return this.model.getChildren().length == 0
    }

    deleteEntry(): void {
        this.jsonVerifyTreeService.deleteEntry(this.model);
    }

}
