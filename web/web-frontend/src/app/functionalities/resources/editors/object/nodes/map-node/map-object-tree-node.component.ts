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
import {MapObjectTreeModel} from "../../model/map-object-tree.model";
import {MapTypeMeta} from "../../../../../../model/text/parts/param-meta/map-type.meta";
import {MapItemObjectTreeModel} from "./item/map-item-object-tree.model";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'map-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        '../nodes.scss',
        '../../../../../../generic/css/tree.scss',
    ]
})
export class MapObjectTreeNodeComponent implements OnInit {

    @Input() model: MapObjectTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    map = new Map();

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        let typeMeta = this.model.typeMeta;

        if (this.model.serverObject) {
            let index = 0;
            for (var prop in this.model.serverObject) {
                if (Object.prototype.hasOwnProperty.call(this.model.serverObject, prop)) {
                    this.model.children.push(
                        new MapItemObjectTreeModel(
                            this.model,
                            index,
                            prop,
                            typeMeta.keyType,
                            this.model.serverObject[prop],
                            typeMeta.valueType
                        )
                    );
                    index++;
                }
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

    shouldDisplayDeleteButton(): boolean {
        return this.model.parentContainer instanceof ListObjectTreeModel;
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.getParent().getChildren(), this.model);
    }

    getTypeForUI(): string {
        let keyTypeMeta = ObjectNodeUtil.getFieldTypeForUI((this.model.typeMeta as MapTypeMeta).keyType);
        let valueTypeMeta = ObjectNodeUtil.getFieldTypeForUI((this.model.typeMeta as MapTypeMeta).valueType);
        return ObjectNodeUtil.getFieldTypeForUI(this.model.typeMeta) +"<" + keyTypeMeta + ", " + valueTypeMeta +">"
    }

    getName(): string {
        return ObjectNodeUtil.getNodeNameForUI(this.model);
    }

    addNewItem() {
        let typeMeta = this.model.typeMeta as MapTypeMeta;
        this.model.children.push(
            new MapItemObjectTreeModel(
                this.model,
                this.model.children.length,
                null,
                typeMeta.keyType,
                null,
                typeMeta.valueType
            )
        )
    }
}
