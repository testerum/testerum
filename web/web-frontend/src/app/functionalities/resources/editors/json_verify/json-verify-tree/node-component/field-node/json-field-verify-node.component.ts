import {Component, Input, OnInit} from '@angular/core';
import {ObjectJsonVerify} from "../../model/object-json-verify.model";
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {JsonTreeChildrenRenderer} from "../../../../../../../generic/components/json-tree/children-renderer/json-tree-children-renderer.abstract";
import {FieldJsonVerify} from "../../model/field-json-verify.model";
import {ArrayJsonVerify} from "../../model/array-json-verify.model";
import {StringJsonVerify} from "../../model/string-json-verify.model";
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {ObjectUtil} from "../../../../../../../utils/object.util";
import {SerializationUtil} from "../../model/util/serialization.util";

@Component({
    moduleId: module.id,
    selector: 'json-field-verify-node',
    templateUrl: 'json-field-verify-node.component.html',
    styleUrls: [
        'json-field-verify-node.component.scss',
        '../../json-verify-tree.generic.scss',
        '../../../../../../../generic/css/tree.scss'
    ]
})
export class JsonFieldVerifyNodeComponent extends JsonTreeChildrenRenderer implements OnInit {

    @Input() model: FieldJsonVerify;
    uiName: string;
    uiValue: string;
    errorName: boolean = false;
    errorValue: boolean = false;

    constructor(jsonVerifyTreeService: JsonVerifyTreeService) {
        super(jsonVerifyTreeService);
    }

    ngOnInit(): void {
        this.uiName = this.getQuotedString(this.model.name);
        this.uiValue = this.model.getValueOfPrimitiveType();
        this.errorName = !this.model.name
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    getNameForUi(): string {
        return this.uiName;
    }

    private getQuotedString(name:string) {
        return name != null ? '"' + name + '"' : '';
    }

    onNameForUiChange(newValue: string): string {
        this.uiName = newValue;
        let value: any = newValue;
        this.model.isDirty = true;

        if(value == "") {
            value = null;
        }

        if (typeof newValue === 'string') {
            newValue = newValue.trim();
            if (newValue.startsWith('"') && newValue.endsWith('"')) {
                value = newValue.substr(1, newValue.length - 2)
            }
        }

        this.errorName = !value;

        this.model.name = value;
        return value;
    }

    onValueForUiChange(newValue: string): JsonTreeNode {
        this.uiValue = newValue;
        let value: any = newValue;
        this.model.isDirty = true;

        if (ObjectUtil.isANumber(newValue)) {
            value = ObjectUtil.getAsNumber(newValue);
        }

        if(value == "") {
            this.model.value = null;
            return null;
        }

        if (newValue === 'null') {
            value = null;
        }

        if(newValue === 'true') {
            value = true;
        }
        if(newValue === 'false') {
            value = false;
        }

        if (typeof newValue === 'string') {
            newValue = newValue.trim();
            if (newValue.startsWith('"') && newValue.endsWith('"')) {
                value = newValue.substr(1, newValue.length - 2)
            }
        }

        try {
            let jsonTreeNode: JsonTreeNode = SerializationUtil.deserialize(value);
            if (!jsonTreeNode || !SerializationUtil.isPrimitiveType(jsonTreeNode)) {
                this.errorValue = true;
                return null;
            }

            this.model.value = jsonTreeNode;

            this.errorValue = false;
            return jsonTreeNode;

        } catch (e) {
            this.errorValue = true;
        }
        return null
    }

    saveName() {
        let name = this.onNameForUiChange(this.uiName);
        this.uiName = this.getQuotedString(name);
    }

    saveValue() {
        let value: any = this.onValueForUiChange(this.uiValue);
        this.uiValue = this.model.getValueOfPrimitiveType();
    }


    createArrayNode() {
        if(this.model.isValueAnArray()) {
            (this.model.value as ArrayJsonVerify).getChildren().push(new ArrayJsonVerify());
        }
        if(this.model.isValueAnObject()) {
            let fieldJsonVerify = new FieldJsonVerify();
            fieldJsonVerify.value = new ArrayJsonVerify();
            (this.model.value as ObjectJsonVerify).getChildren().push(fieldJsonVerify);
        }
    }

    createObjectNode() {
        if(this.model.isValueAnArray()) {
            (this.model.value as ArrayJsonVerify).getChildren().push(new ObjectJsonVerify());
        }
        if(this.model.isValueAnObject()) {
            let fieldJsonVerify = new FieldJsonVerify();
            fieldJsonVerify.value = new ObjectJsonVerify();
            (this.model.value as ObjectJsonVerify).getChildren().push(fieldJsonVerify);
        }
    }

    createPrimitiveNode() {
        if(this.model.isValueAnArray()) {
            (this.model.value as ArrayJsonVerify).getChildren().push(new StringJsonVerify())
        }
        if(this.model.isValueAnObject()) {
            let fieldJsonVerify = new FieldJsonVerify();
            fieldJsonVerify.value = new StringJsonVerify();
            (this.model.value as ObjectJsonVerify).getChildren().push(fieldJsonVerify)
        }
    }

    deleteEntry() {
        this.jsonVerifyTreeService.deleteEntry(this.model)
    }

    onCompareModeChange(compareMode: CompareMode) {
        if(compareMode instanceof CompareMode) {
            this.model.compareMode = compareMode;
        }
    }

    shouldHideCompareMode(): boolean {
        if(!this.model.isValueAContainer()) {
            return true;
        }

        if(this.jsonVerifyTreeService.editMode) {
            if(this.model.compareMode != CompareMode.INHERIT) {
                return false;
            }
            return !this.hasMouseOver;
        }

        return this.model.compareMode == CompareMode.INHERIT;
    }

    displayEditModeForCompareMode(): boolean {
        if(this.jsonVerifyTreeService.editMode) {
            if(this.hasMouseOver) {
                return true;
            }
            else return false;
        }
        return false;
    }
}
