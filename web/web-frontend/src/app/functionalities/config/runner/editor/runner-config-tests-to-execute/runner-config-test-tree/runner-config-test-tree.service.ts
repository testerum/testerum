import {EventEmitter, Injectable} from "@angular/core";
import {RunnerConfigTestTreeUtil} from "./util/runner-config-test-tree.util";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeExpandUtil} from "../../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../../../../model/feature/tree/root-feature-node.model";
import {FeatureService} from "../../../../../../service/feature.service";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {RunnerConfigTestTreeBaseModel} from "./model/runner-config-test-tree-base.model";
import {RunnerConfigTestTreeContainerModel} from "./model/runner-config-test-tree-container.model";

@Injectable()
export class RunnerConfigTestTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();
    treeFilter: FeaturesTreeFilter = FeaturesTreeFilter.createEmptyFilter();
    selectedPaths: Array<Path> = [];

    refreshTreeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor(private jsonTreeService: JsonTreeService,
                private featureService: FeatureService) {
    }

    initializeTreeFromServer(selectedPaths: Array<Path>, expandToLevel: number = 2) {
        this.selectedPaths = selectedPaths;

        this.featureService.getFeatureTree(this.treeFilter).subscribe(
            (rootNode: RootFeatureNode) => {
                RunnerConfigTestTreeUtil.mapServerModelToTreeModel(rootNode, this.treeModel);

                JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, expandToLevel);

                let allTreeNodes: RunnerConfigTestTreeBaseModel[] = this.treeModel.getAllTreeNodes<RunnerConfigTestTreeBaseModel>();
                for (const selectedPath of this.selectedPaths) {
                    let nodeToSelect = this.getNodeByPath(allTreeNodes, selectedPath);
                    if (nodeToSelect != null) {
                        nodeToSelect.setSelected(true);
                    }
                }

                this.refreshTreeEventEmitter.emit();
            }
        )
    }

    private getNodeByPath(allTreeNodes: RunnerConfigTestTreeBaseModel[], selectedPath: Path): RunnerConfigTestTreeBaseModel|null {
        for (const treeNode of allTreeNodes) {
            if (treeNode.path.equals(selectedPath)) {
                return treeNode;
            }
        }
        return null;
    }

    recalculateSelectedPathsFromTreeModel() {
        this.selectedPaths.length = 0;
        for (const rootNodes of this.treeModel.children) {
            this.addSelectedPathsOfNode(rootNodes as RunnerConfigTestTreeBaseModel);
        }
    }

    private addSelectedPathsOfNode(treeNode: RunnerConfigTestTreeBaseModel) {
        if(treeNode.isSelectedNode()) {
            this.selectedPaths.push(treeNode.path);
            return;
        }

        if (treeNode instanceof RunnerConfigTestTreeContainerModel) {
            for (const child of treeNode.children) {
                this.addSelectedPathsOfNode(child);
            }
        }
    }
}
