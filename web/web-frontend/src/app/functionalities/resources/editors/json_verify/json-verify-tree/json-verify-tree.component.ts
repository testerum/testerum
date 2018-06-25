import {Component, OnInit} from '@angular/core';
import {ArrayJsonVerify} from "./model/array-json-verify.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {JsonArrayVerifyNodeComponent} from "./node-component/array-node/json-array-verify-node.component";
import {JsonObjectVerifyNodeComponent} from "./node-component/object-node/json-object-verify-node.component";
import {ObjectJsonVerify} from "./model/object-json-verify.model";
import {JsonVerifyTreeService} from "./json-verify-tree.service";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {SerializationUtil} from "./model/util/serialization.util";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {FieldJsonVerify} from "./model/field-json-verify.model";
import {JsonFieldVerifyNodeComponent} from "./node-component/field-node/json-field-verify-node.component";
import {StringJsonVerify} from "./model/string-json-verify.model";
import {JsonPrimitiveVerifyNodeComponent} from "./node-component/primitive-node/json-primitive-verify-node.component";
import {BooleanJsonVerify} from "./model/boolean-json-verify.model";
import {NullJsonVerify} from "./model/null-json-verify.model";
import {NumberJsonVerify} from "./model/number-json-verify.model";
import {EmptyJsonVerify} from "./model/empty-json-verify.model";
import {JsonEmptyVerifyNodeComponent} from "./node-component/emtpy-node/json-empty-verify-node.component";

@Component({
    moduleId: module.id,
    selector: 'json-verify-tree',
    templateUrl: 'json-verify-tree.component.html'
})

export class JsonVerifyTreeComponent implements OnInit {

    treeModel: JsonTreeModel;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ArrayJsonVerify, JsonArrayVerifyNodeComponent)
        .addPair(ObjectJsonVerify, JsonObjectVerifyNodeComponent)
        .addPair(FieldJsonVerify, JsonFieldVerifyNodeComponent)
        .addPair(BooleanJsonVerify, JsonPrimitiveVerifyNodeComponent)
        .addPair(NullJsonVerify, JsonPrimitiveVerifyNodeComponent)
        .addPair(NumberJsonVerify, JsonPrimitiveVerifyNodeComponent)
        .addPair(StringJsonVerify, JsonPrimitiveVerifyNodeComponent)
        .addPair(EmptyJsonVerify, JsonEmptyVerifyNodeComponent);

    constructor(private verifyTreeService:JsonVerifyTreeService) {
    }

    ngOnInit() {
        this.treeModel = this.verifyTreeService.rootNode;
    }
}
