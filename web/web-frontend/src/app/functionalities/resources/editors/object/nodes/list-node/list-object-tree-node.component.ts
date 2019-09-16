import {ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ObjectResourceComponentService} from "../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../utils/array.util";
import {ObjectNodeUtil} from "../util/object-node.util";
import {EnumObjectTreeModel} from "../../model/enum-object-tree.model";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../../../../../model/prime-ng/StringSelectItem";
import {BooleanObjectTreeModel} from "../../model/boolean-object-tree.model";
import {ListObjectTreeModel} from "../../model/list-object-tree.model";
import {ObjectTypeMeta} from "../../../../../../model/text/parts/param-meta/object-type.meta";
import {ListTypeMeta} from "../../../../../../model/text/parts/param-meta/list-type.meta";
import {ObjectUtil} from "../../../../../../utils/object.util";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'list-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class ListObjectTreeNodeComponent implements OnInit {

    @Input() model: ListObjectTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        let listTypeMeta = this.model.typeMeta as ListTypeMeta;

        if (this.model.serverObject) {
            for (let i = 0; i < this.model.serverObject.length; i++) {
                const item = this.model.serverObject[i];
                this.objectResourceComponentService.addFieldToObjectTree(
                    this.model,
                    listTypeMeta.itemsType,
                    "item " + i,
                    item
                )
            }
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

    getTypeForUI(): string {
        let listTypeMeta = ObjectNodeUtil.getFieldTypeForUI((this.model.typeMeta as ListTypeMeta).itemsType);
        return ObjectNodeUtil.getFieldTypeForUI(this.model.typeMeta) +"<" + listTypeMeta +">"
    }

    getName(): string {
        return ObjectNodeUtil.getNodeNameForUI(this.model);
    }

    addNewItem() {
        let listTypeMeta = this.model.typeMeta as ListTypeMeta;

        this.objectResourceComponentService.addFieldToObjectTree(
            this.model,
            listTypeMeta.itemsType,
            "item " + this.model.getChildren().length,
            null
        )
    }
}
