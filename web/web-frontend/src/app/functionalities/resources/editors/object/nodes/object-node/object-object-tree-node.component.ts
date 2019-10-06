import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectObjectTreeModel} from "../../model/object-object-tree.model";
import {ObjectTypeMeta} from "../../../../../../model/text/parts/param-meta/object-type.meta";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {ObjectNodeUtil} from "../util/object-node.util";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'object-object-tree-node.component.html',
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ObjectObjectTreeNodeComponent implements OnInit {

    @Input() model: ObjectObjectTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
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
