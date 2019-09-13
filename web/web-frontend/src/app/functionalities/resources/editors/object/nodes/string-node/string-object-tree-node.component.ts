import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {StringObjectTreeModel} from "../../model/string-object-tree.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'string-object-tree-node.component.html',
    styleUrls: [
        '../../../../../../generic/css/tree.scss',
    ]
})
export class StringObjectTreeNodeComponent implements OnInit{

    @Input() model: StringObjectTreeModel;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        console.log("STRING TREE NODE")
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
}
