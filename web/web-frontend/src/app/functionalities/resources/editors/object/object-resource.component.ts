import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges
} from '@angular/core';
import {ResourceComponent} from "../resource-component.interface";
import {ParamStepPatternPart} from "../../../../model/text/parts/param-step-pattern-part.model";
import {NgForm} from "@angular/forms";
import {ObjectResourceComponentService} from "./object-resource.component-service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {StringObjectTreeModel} from "./model/string-object-tree.model";
import {StringObjectTreeNodeComponent} from "./nodes/primitive-node/string-object-tree-node.component";
import {TextTypeMeta} from "../../../../model/text/parts/param-meta/text-type.meta";
import {ObjectResourceModel} from "./object-resource.model";
import {ObjectTreeModel} from "./model/object-tree.model";

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
        cancel() {
        }

        save() {
        }
    };

    treeModel: JsonTreeModel = new JsonTreeModel();
    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(StringObjectTreeModel, StringObjectTreeNodeComponent);

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
        if(this.treeModel.children.length == 0) {
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
        if (serverType instanceof TextTypeMeta) {
            let stringObjectTreeModel = new StringObjectTreeModel(this.treeModel, this.stepParameter.name, this.model.content, serverType);
            this.treeModel.children.push(stringObjectTreeModel)
        }
    }

    isFormValid(): boolean {
        return true;
    }

    getForm(): NgForm {
        return null;
    }
}
