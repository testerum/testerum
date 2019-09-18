import {ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../../../../../model/prime-ng/StringSelectItem";
import {BooleanObjectTreeModel} from "../../model/boolean-object-tree.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {IdUtils} from "../../../../../../utils/id.util";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'boolean-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class BooleanObjectTreeNodeComponent implements OnInit {

    @Input() model: BooleanObjectTreeModel;

    @ViewChild('booleanInput', { static: false }) inputElementRef: ElementRef;
    private tempValueHolder: string;

    id = IdUtils.getTemporaryId();
    possibleValues: string[] = [];

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        this.possibleValues.push("true");
        this.possibleValues.push("false");
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
    }

    onValueChange(newValue: string) {
        this.model.value = newValue;
        this.refresh();
    }

    onInputEvent(event: MouseEvent) {
        this.tempValueHolder = this.inputElementRef.nativeElement.value;
        this.inputElementRef.nativeElement.value = '';
        let that = this;
        setTimeout(() => {
            this.inputElementRef.nativeElement.value = this.tempValueHolder;
        }, 10);
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
}
