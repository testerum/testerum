import {ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {BooleanObjectTreeModel} from "../../model/boolean-object-tree.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {DateUtil} from "../../../../../../utils/date.util";
import {StringUtils} from "../../../../../../utils/string-utils.util";

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
        if (StringUtils.isNotEmpty(this.model.value)) {
            this.date = DateUtil.isoDateStringToDate(this.model.value);
            this.inputDate = DateUtil.dateToIsoDateString(this.date);
        }
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
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
        this.date = event;
        this.inputDate = DateUtil.dateToIsoDateString(this.date);
        this.model.value = DateUtil.dateToIsoDateString(this.date);
        this.refresh();
    }

    inputChange(event: any) {
        this.inputDate = event;
        this.date = DateUtil.isoDateStringToDate(event);
        this.model.value = DateUtil.dateToIsoDateString(this.date);
    }

    getDescription(): string {
        return "You can specify a date using the following format:\n" +
            "<code>mm-dd-yyyy hh:mm:ss</code>\n" +
            "mm - month, mandatory field\n" +
            "dd - day, mandatory field\n" +
            "yyyy - year, mandatory field\n" +
            "hh - hour, optional field\n" +
            "mm - minutes, optional field\n" +
            "ss - seconds, optional field\n" +
            "\n" +
            "You can also use variables from the context.\n" +
            "e.g.: <code>{{variable_name}}</code>"
    }
}
