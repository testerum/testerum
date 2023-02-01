import {Component, Input} from '@angular/core';
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {JsonTreeNodeAbstract} from "../../../../json-tree/model/json-tree-node.abstract";
import {StringJsonVerify} from "../../model/string-json-verify.model";
import {NumberJsonVerify} from "../../model/number-json-verify.model";
import {NullJsonVerify} from "../../model/null-json-verify.model";
import {BooleanJsonVerify} from "../../model/boolean-json-verify.model";
import {SerializationUtil} from "../../model/util/serialization.util";
import {JsonTreeNode} from "../../../../json-tree/model/json-tree-node.model";
import {ObjectUtil} from "../../../../../../utils/object.util";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'json-primitive-verify-node.component.html',
    styleUrls: [
        '../../json-verify-tree.generic.scss',
        '../../../../../css/tree.scss'
    ]
})
export class JsonPrimitiveVerifyNodeComponent {

    @Input() model: JsonTreeNodeAbstract;

    uiValue: string;
    errorValue: boolean = false;

    hasMouseOver: boolean = false;

    jsonVerifyTreeService: JsonVerifyTreeService;

    constructor(jsonVerifyTreeService: JsonVerifyTreeService) {
        this.jsonVerifyTreeService = jsonVerifyTreeService;
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    deleteEntry(): void {
        this.jsonVerifyTreeService.deleteEntry(this.model);
    }

    getValueOfPrimitiveType(): string {

        if (this.model instanceof BooleanJsonVerify) {
            return (this.model as BooleanJsonVerify).value ? "true" : "false";
        }
        if (this.model instanceof NullJsonVerify) {
            return "null";
        }
        if (this.model instanceof NumberJsonVerify) {
            return "" + ((this.model as NumberJsonVerify).value);
        }
        if (this.model instanceof StringJsonVerify) {
            let value: string = (this.model as StringJsonVerify).value;
            return value ? '"' + (value) + '"' : '';
        }

        throw new Error("unknown JSON primitive type");
    }

    onValueChange(newValue: string): JsonTreeNode {
        this.uiValue = newValue;

        let value: any = newValue;
        if (!value) {
            value = undefined;
        }

        if (ObjectUtil.isANumber(newValue)) {
            value = ObjectUtil.getAsNumber(newValue);
        }

        if (newValue == 'null') {
            value = null;
        }

        if (newValue === 'true') {
            value = true;
        }
        if (newValue === 'false') {
            value = false;
        }

        if (typeof newValue === 'string') {
            newValue = newValue.trim();
            if (newValue.startsWith('"') && newValue.endsWith('"')) {
                value = newValue.substr(1, newValue.length - 2)
            }
        }

        try {
            let jsonTreeNode: JsonTreeNode = SerializationUtil.deserialize(value, this.model.parentContainer);
            if (!jsonTreeNode || !SerializationUtil.isPrimitiveType(jsonTreeNode)) {
                return null;
            }

            this.errorValue = false;
            return jsonTreeNode;

        } catch (e) {
            this.errorValue = true;
        }
        return null
    }

    saveValueToTree() {
        let newNode = this.onValueChange(this.uiValue);
        if (!newNode) {
            this.jsonVerifyTreeService.deleteEntry(this.model);
        }
        this.jsonVerifyTreeService.replaceNode(this.model, newNode);
    }
}
