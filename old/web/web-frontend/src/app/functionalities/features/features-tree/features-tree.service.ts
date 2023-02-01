import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
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

    isPasteATest(): boolean {
        if(this.pathToCopy && this.pathToCopy.isFile()) return true;
        if(this.pathToCut && this.pathToCut.isFile()) return true;
        return false;
    }
}
