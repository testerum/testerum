
import {
    AfterViewInit,
    ComponentFactory, ComponentFactoryResolver, ComponentRef, Input, OnInit, ViewChild,
    ViewContainerRef
} from "@angular/core";
import {JsonChildrenComponent} from "../json-children/json-children.component";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {JsonTreeNodeState} from "../model/json-tree-node-state.model";
import {ObjectJsonVerify} from "../../../../functionalities/resources/editors/json_verify/json-verify-tree/model/object-json-verify.model";
import {ArrayJsonVerify} from "../../../../functionalities/resources/editors/json_verify/json-verify-tree/model/array-json-verify.model";
import {JsonVerifyTreeService} from "../../../../functionalities/resources/editors/json_verify/json-verify-tree/json-verify-tree.service";

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
