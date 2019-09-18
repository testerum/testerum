import {ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {MapItemObjectTreeModel} from "./map-item-object-tree.model";
import {ModelComponentMapping} from "../../../../../../../model/infrastructure/model-component-mapping.model";
import {ObjectResourceComponentService} from "../../../object-resource.component-service";
import {ArrayUtil} from "../../../../../../../utils/array.util";

@Component({
    moduleId: module.id,
    selector: 'json-string-verify-node',
    templateUrl: 'map-item-object-tree-node.component.html',
    encapsulation: ViewEncapsulation.None,
    styleUrls: [
        '../../nodes.scss',
        '../../../../../../../generic/css/tree.scss',
    ]
})
export class MapItemObjectTreeNodeComponent implements OnInit {

    @Input() model: MapItemObjectTreeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    map = new Map();

    hasMouseOver: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
    }

    ngOnInit(): void {
        this.objectResourceComponentService.addFieldToObjectTree(
            this.model,
            this.model.keyTypeMeta,
            "key",
            this.model.keyObject
        );
        this.objectResourceComponentService.addFieldToObjectTree(
            this.model,
            this.model.valueTypeMeta,
            "value",
            this.model.valueObject
        );
    }

    isEditMode(): boolean {
        return this.objectResourceComponentService.editMode;
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    deleteEntry(): void {
        ArrayUtil.removeElementFromArray(this.model.getParent().getChildren(), this.model);
    }

    getName(): string {
        return "item " + this.model.parentContainer.getChildren().indexOf(this.model);
    }
}
