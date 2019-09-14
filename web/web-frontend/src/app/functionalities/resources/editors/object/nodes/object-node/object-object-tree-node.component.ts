import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {StringObjectTreeModel} from "../../model/string-object-tree.model";
import {ObjectObjectTreeModel} from "../../model/object-object-tree.model";
import {ObjectTypeMeta} from "../../../../../../model/text/parts/param-meta/object-type.meta";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'object-object-tree-node.component.html',
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ObjectObjectTreeNodeComponent implements OnInit{

    @Input() model: ObjectObjectTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        let objectTypeMeta = this.model.typeMeta as ObjectTypeMeta;

        for (const field of objectTypeMeta.fields) {
            let fieldValue = this.model.serverObject ? this.model.serverObject[field.name] : null;
            this.objectResourceComponentService.addFieldToObjectTree (
                this.model,
                field.type,
                field.name,
                fieldValue
            )
        }
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.parentContainer.getParent().getChildren(), this.model);
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }
}
