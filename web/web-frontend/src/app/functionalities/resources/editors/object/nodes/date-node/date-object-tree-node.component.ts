import {ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {BooleanObjectTreeModel} from "../../model/boolean-object-tree.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {DateUtil} from "../../../../../../utils/date.util";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'date-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        "date-object-tree-node.component.scss",
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class DateObjectTreeNodeComponent implements OnInit {

    @Input() model: BooleanObjectTreeModel;

    date: Date;
    inputDate: string;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        this.date = DateUtil.stringToDate(this.model.value);
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
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

    shouldDisplayDeleteButton(): boolean {
        return this.model.parentContainer instanceof ListObjectTreeModel;
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.getParent().getChildren(), this.model);
    }

    getTypeForUI(): string {
        return ObjectNodeUtil.getFieldTypeForUI(this.model.typeMeta)
    }

    getName(): string {
        return ObjectNodeUtil.getNodeNameForUI(this.model);
    }

    onDateSelect(event: Date) {
        this.inputDate = DateUtil.dateTimeToShortString(event);
        this.date = event;
        this.model.value = this.inputDate;
    }

    inputChange(event: any) {
        this.inputDate = event;
        this.date = DateUtil.stringToDate(event);
        this.model.value = event;
    }
}
