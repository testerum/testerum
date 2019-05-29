import {EventEmitter, Injectable} from "@angular/core";
import {RunConfigTestTreeUtil} from "./util/run-config-test-tree.util";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeService} from "../../../../../../generic/components/json-tree/json-tree.service";
import {JsonTreeExpandUtil} from "../../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {FeaturesTreeFilter} from "../../../../../../model/feature/filter/features-tree-filter.model";
import {RootFeatureNode} from "../../../../../../model/feature/tree/root-feature-node.model";
import {FeatureService} from "../../../../../../service/feature.service";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {RunConfigTestTreeBaseModel} from "./model/run-config-test-tree-base.model";
import {RunConfigTestTreeContainerModel} from "./model/run-config-test-tree-container.model";

@Injectable()
export class RunConfigTestTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();
    treeFilter: FeaturesTreeFilter = FeaturesTreeFilter.createEmptyFilter();
    paths: Array<Path> = [];

    refreshTreeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor(private jsonTreeService: JsonTreeService,
                private featureService: FeatureService) {
    }

    initializeTreeFromServer(paths: Array<Path>, expandToLevel: number = 2) {
        this.paths = paths;

        this.featureService.getFeatureTree(this.treeFilter).subscribe(
            (rootNode: RootFeatureNode) => {
                RunConfigTestTreeUtil.mapServerModelToTreeModel(rootNode, this.treeModel);

                JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, expandToLevel);

                let allTreeNodes: RunConfigTestTreeBaseModel[] = this.treeModel.getAllTreeNodes<RunConfigTestTreeBaseModel>();
                for (const selectedPath of this.paths) {
                    let nodeToSelect = this.getNodeByPath(allTreeNodes, selectedPath);
                    if (nodeToSelect != null) {
                        nodeToSelect.setSelected(true);
                    }
                }

                this.refreshTreeEventEmitter.emit();
            }
        )
    }

    private getNodeByPath(allTreeNodes: RunConfigTestTreeBaseModel[], selectedPath: Path): RunConfigTestTreeBaseModel|null {
        for (const treeNode of allTreeNodes) {
            if (treeNode.path.equals(selectedPath)) {
                return treeNode;
            }
        }
        return null;
    }

    recalculatePathsFromTreeModel() {
        this.paths.length = 0;
        for (const rootNodes of this.treeModel.children) {
            this.addPathsOfNode(rootNodes as RunConfigTestTreeBaseModel);
        }
    }

    private addPathsOfNode(treeNode: RunConfigTestTreeBaseModel) {
        if(treeNode.isSelectedNode()) {
            this.paths.push(treeNode.path);
            return;
        }

        if (treeNode instanceof RunConfigTestTreeContainerModel) {
            for (const child of treeNode.children) {
                this.addPathsOfNode(child);
            }
        }
    }
}
