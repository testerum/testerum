import {ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {EnumObjectTreeModel} from "../../model/enum-object-tree.model";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../../../../../model/prime-ng/StringSelectItem";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'enum-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class EnumObjectTreeNodeComponent implements OnInit {

    @Input() model: EnumObjectTreeModel;

    possibleValues: SelectItem[] = [];
    selectedValue: string;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        let enumTypeMeta = this.model.typeMeta;
        if (enumTypeMeta) {
            for (const possibleValue of enumTypeMeta.possibleValues) {
                let enumSelectItem = new StringSelectItem(
                    possibleValue
                );
                this.possibleValues.push(enumSelectItem)
            }
        }
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.parentContainer.getParent().getChildren(), this.model);
    }

    onValueChange(newValue: string) {
        this.model.value = newValue;
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    getTypeForUI(): string {
        return ObjectNodeUtil.getFieldTypeForUI(this.model.typeMeta)
    }
}
