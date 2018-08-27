import {Component, Input, OnInit} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {ManualExecPlan} from "../../model/manual-exec-plan.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualExecPlansService} from "../../../service/manual-exec-plans.service";
import {RootFeatureNode} from "../../../../../model/feature/tree/root-feature-node.model";
import ManualSelectTestsTreeUtil from "./util/manual-select-tests-tree.util";
import {SelectTestsTreeContainerModel} from "./model/select-tests-tree-container.model";
import {SelectTestsTreeNodeModel} from "./model/select-tests-tree-node.model";
import {ManualSelectTestsTreeComponentService} from "./manual-select-tests-tree.component-service";
import {ManualSelectTestsContainerComponent} from "./container/manual-select-tests-container.component";
import {ManualSelectTestsNodeComponent} from "./container/node/manual-select-tests-node.component";

@Component({
    selector: 'manual-select-tests-tree',
    templateUrl: 'manual-select-tests-tree.component.html',
    providers: [ManualSelectTestsTreeComponentService]
})
export class ManualSelectTestsTreeComponent implements OnInit {
    @Input() model: ManualExecPlan;
    @Input() isEditMode: boolean;


    treeModel: JsonTreeModel = ManualSelectTestsTreeUtil.createRootPackage();
    manualSelectTreeComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(SelectTestsTreeContainerModel, ManualSelectTestsContainerComponent)
        .addPair(SelectTestsTreeNodeModel, ManualSelectTestsNodeComponent);

    constructor(private manualExecPlansService: ManualExecPlansService,
                private manualSelectTestsTreeComponentService: ManualSelectTestsTreeComponentService) {
    }

    ngOnInit() {
        this.manualSelectTestsTreeComponentService.isEditMode = this.isEditMode;

        this.manualExecPlansService.getAllManualTests().subscribe((rootFeatureNode: RootFeatureNode) => {
            ManualSelectTestsTreeUtil.mapFeaturesWithTestsToTreeModel(this.treeModel, rootFeatureNode)
        })
    }
}
