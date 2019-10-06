import {AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ResourceComponent} from "../resource-component.interface";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {NgForm} from "@angular/forms";
import {ObjectResourceComponentService} from "./object-resource.component-service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {StringObjectTreeModel} from "./model/string-object-tree.model";
import {StringObjectTreeNodeComponent} from "./nodes/string-node/string-object-tree-node.component";
import {ObjectResourceModel} from "./object-resource.model";
import {ObjectObjectTreeModel} from "./model/object-object-tree.model";
import {JsonUtil} from "../../../../utils/json.util";
import {ObjectObjectTreeNodeComponent} from "./nodes/object-node/object-object-tree-node.component";
import {ObjectTreeModel} from "./model/interfaces/object-tree.model";
import {EnumObjectTreeModel} from "./model/enum-object-tree.model";
import {EnumObjectTreeNodeComponent} from "./nodes/enum-node/enum-object-tree-node.component";
import {BooleanObjectTreeModel} from "./model/boolean-object-tree.model";
import {BooleanObjectTreeNodeComponent} from "./nodes/boolean-node/boolean-object-tree-node.component";
import {DateObjectTreeModel} from "./model/date-object-tree.model";
import {DateObjectTreeNodeComponent} from "./nodes/date-node/date-object-tree-node.component";
import {ListObjectTreeModel} from "./model/list-object-tree.model";
import {ListObjectTreeNodeComponent} from "./nodes/list-node/list-object-tree-node.component";
import {MapObjectTreeNodeComponent} from "./nodes/map-node/map-object-tree-node.component";
import {MapObjectTreeModel} from "./model/map-object-tree.model";
import {MapItemObjectTreeNodeComponent} from "./nodes/map-node/item/map-item-object-tree-node.component";
import {MapItemObjectTreeModel} from "./nodes/map-node/item/map-item-object-tree.model";
import {InstantTypeMeta} from "../../../../model/text/parts/param-meta/instant-type-meta.model";
import {LocalDateTimeTypeMeta} from "../../../../model/text/parts/param-meta/local-date-time-type-meta.model";
import {LocalDateTypeMeta} from "../../../../model/text/parts/param-meta/local-date-type-meta.model";

@Component({
    selector: 'object-resource',
    templateUrl: './object-resource.component.html',
    styleUrls: ['./object-resource.component.scss'],
    providers: [ObjectResourceComponentService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ObjectResourceComponent extends ResourceComponent<any> implements OnInit {

    @Input() name: string;
    @Input() model: ObjectResourceModel;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() isSharedResource: boolean = false;
    @Input() contextActions: ResourceContextActions = new class implements ResourceContextActions {
        cancel() {}
        save() {}
    };

    treeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(BooleanObjectTreeModel, BooleanObjectTreeNodeComponent)
        .addPair(DateObjectTreeModel, DateObjectTreeNodeComponent)
        .addPair(EnumObjectTreeModel, EnumObjectTreeNodeComponent)
        .addPair(ListObjectTreeModel, ListObjectTreeNodeComponent)
        .addPair(MapObjectTreeModel, MapObjectTreeNodeComponent)
        .addPair(MapItemObjectTreeModel, MapItemObjectTreeNodeComponent)
        .addPair(StringObjectTreeModel, StringObjectTreeNodeComponent)
        .addPair(ObjectObjectTreeModel, ObjectObjectTreeNodeComponent);

    constructor(private cd: ChangeDetectorRef,
                private objectResourceComponentService: ObjectResourceComponentService) {
        super();
    }

    ngOnInit() {
        this.objectResourceComponentService.editMode = this.editMode;
        this.initTree();
        this.refreshChangeDetector();
    }

    onBeforeSave(): void {
        if (this.treeModel.children.length == 0 || this.isEmptyTree()) {
            this.model.content = "";
        }
        let firstObjectRootElement: ObjectTreeModel = this.treeModel.children[0] as ObjectTreeModel;
        this.model.content = firstObjectRootElement.serialize();

    }

    refresh() {
        this.initTree();
        this.refreshChangeDetector();
    }

    private refreshChangeDetector() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    private initTree() {
        if (!this.stepParameter) {
            return
        }
        this.treeModel.children = [];
        let serverType = this.stepParameter.serverType;

        let objectName = this.stepParameter.name;
        let serverObject = JsonUtil.parseJson(this.model.content);

        this.objectResourceComponentService.addFieldToObjectTree(this.treeModel, serverType, objectName, serverObject, this.condensedViewMode, true);
    }

    isFormValid(): boolean {
        return true;
    }

    getForm(): NgForm {
        return null;
    }

    private isEmptyTree(): boolean {
        for (const child of this.treeModel.children) {
            if(!(child as ObjectTreeModel).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    shouldShowTree(): boolean {
        return this.condensedViewMode && this.model.isEmpty()
    }
}
