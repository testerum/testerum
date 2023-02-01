import {ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {EnumObjectTreeModel} from "../../model/enum-object-tree.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {IdUtils} from "../../../../../../utils/id.util";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'enum-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        'enum-object-tree-node.component.scss',
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class EnumObjectTreeNodeComponent implements OnInit {

    @Input() model: EnumObjectTreeModel;

    @ViewChild('enumInput') inputElementRef: ElementRef;
    private tempValueHolder: string;

    id = IdUtils.getTemporaryId();
    possibleValues: string[] = [];

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        let enumTypeMeta = this.model.typeMeta;
        if (enumTypeMeta) {
            for (const possibleValue of enumTypeMeta.possibleValues) {
                this.possibleValues.push(possibleValue)
            }
        }
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

    getTypeForUI(): string {
        return ObjectNodeUtil.getFieldTypeForUI(this.model.typeMeta)
    }

    getName(): string {
        return ObjectNodeUtil.getNodeNameForUI(this.model);
    }
}
