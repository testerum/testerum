import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import FeaturesTreeUtil from "./util/features-tree.util";
import {FeatureService} from "../../../service/feature.service";
import {RootFeatureNode} from "../../../model/feature/tree/root-feature-node.model";
import {FeaturesTreeFilter} from "../../../model/feature/filter/features-tree-filter.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";

@Injectable()
export class FeaturesTreeService {

    treeModel: JsonTreeModel;
    treeFilter: FeaturesTreeFilter = FeaturesTreeFilter.createEmptyFilter();

    pathToCut: Path = null;
    pathToCopy: Path = null;

    constructor(private jsonTreeService: JsonTreeService,
                private featureService: FeatureService) {
    }

    initializeTestsTreeFromServer(selectedPath: Path, expandToLevel: number = 2) {
        this.featureService.getFeatureTree(this.treeFilter).subscribe(
            (rootNode: RootFeatureNode) => {
                let newJsonTree = FeaturesTreeUtil.mapServerTreeToFeaturesTreeModel(rootNode);

                JsonTreeExpandUtil.expandTreeToLevel(newJsonTree, expandToLevel);
                let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(newJsonTree, selectedPath);
                if (selectedNode && selectedNode.isContainer()) {
                    (selectedNode as FeatureTreeContainerModel).getNodeState().showChildren = true
                }
                this.sort(newJsonTree);

                this.treeModel = newJsonTree;

                this.jsonTreeService.setSelectedNode(selectedNode);
            }
        )
    }

    selectNodeAtPath(path: Path) {
        if (this.treeModel) {
            let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.treeModel, path);
            this.jsonTreeService.setSelectedNode(selectedNode);
        }
    }

    private sort(testsJsonTreeModel: JsonTreeModel): void {
        this.sortChildren(testsJsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: TestTreeNodeModel, right: TestTreeNodeModel) => {
            if (left.isContainer() && !right.isContainer()) {
                return -1;
            }

            if (!left.isContainer() && right.isContainer()) {
                return 1;
            }

            if (left.name.toUpperCase() < right.name.toUpperCase()) return -1;
            if (left.name.toUpperCase() > right.name.toUpperCase()) return 1;

            return 0;
        });

        children.forEach(it => {
            if (it.isContainer()) {
                this.sortChildren((it as FeatureTreeContainerModel).children)
            }
        })
    }

    setPathToCut(path: Path) {
        this.pathToCopy = null;
        this.pathToCut = path;
    }

    setPathToCopy(path: Path) {
        this.pathToCopy = path;
        this.pathToCut = null;
    }

    canPaste(path: Path): boolean {
        return (this.pathToCopy != null && !this.pathToCopy.equals(path))
            || (this.pathToCut != null && !this.pathToCut.equals(path));
    }
}
