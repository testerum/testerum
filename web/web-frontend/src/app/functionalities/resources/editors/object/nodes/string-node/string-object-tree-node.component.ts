import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {StringObjectTreeModel} from "../../model/string-object-tree.model";
import {ObjectNodeUtil} from "../util/object-node.util";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'string-object-tree-node.component.html',
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class StringObjectTreeNodeComponent {

    @Input() model: StringObjectTreeModel;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
    }

    shouldDisplayDeleteButton(): boolean {
        return this.model.parentContainer instanceof ListObjectTreeModel;
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.getParent().getChildren(), this.model);
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

    getName(): string {
        return ObjectNodeUtil.getNodeNameForUI(this.model);
    }
}
