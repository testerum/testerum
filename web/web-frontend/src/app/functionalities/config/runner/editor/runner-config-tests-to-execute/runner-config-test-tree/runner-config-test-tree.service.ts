import {EventEmitter, Injectable} from "@angular/core";
import {RunnerConfigTestTreeUtil} from "./util/runner-config-test-tree.util";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeExpandUtil} from "../../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../../../../model/feature/tree/root-feature-node.model";
import {FeatureService} from "../../../../../../service/feature.service";

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
