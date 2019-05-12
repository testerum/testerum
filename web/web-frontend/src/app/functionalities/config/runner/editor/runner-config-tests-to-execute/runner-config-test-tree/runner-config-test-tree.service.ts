import {EventEmitter, Injectable} from "@angular/core";
import {RunnerConfigTestTreeFilterModel} from "./model/filter/runner-config-test-tree-filter.model";
import {RunnerConfigTestTreeUtil} from "./util/runner-config-test-tree.util";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {ManualTestPlansService} from "../../../../../manual/service/manual-test-plans.service";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ManualTestsStatusTreeRoot} from "../../../../../manual/plans/model/status-tree/manual-tests-status-tree-root.model";
import {JsonTreeExpandUtil} from "../../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../../../../model/feature/tree/root-feature-node.model";
import FeaturesTreeUtil from "../../../../../features/features-tree/util/features-tree.util";
import {FeatureTreeContainerModel} from "../../../../../features/features-tree/model/feature-tree-container.model";
import {FeatureService} from "../../../../../../service/feature.service";
import {JsonTreeNode} from "../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {TestTreeNodeModel} from "../../../../../features/features-tree/model/test-tree-node.model";
import {RunnerConfig} from "../../../model/runner-config.model";

@Injectable()
export class RunnerConfigTestTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();
    treeFilter: FeaturesTreeFilter = FeaturesTreeFilter.createEmptyFilter();

    refreshTreeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor(private jsonTreeService: JsonTreeService,
                private featureService: FeatureService) {
    }

    initializeTreeFromServer(expandToLevel: number = 2) {
        this.featureService.getFeatureTree(this.treeFilter).subscribe(
            (rootNode: RootFeatureNode) => {
                RunnerConfigTestTreeUtil.mapServerModelToTreeModel(rootNode, this.treeModel);

                JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, expandToLevel);

                this.refreshTreeEventEmitter.emit();
            }
        )
    }
}
