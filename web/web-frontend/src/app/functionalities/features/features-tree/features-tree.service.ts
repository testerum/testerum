import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../generic/components/json-tree/util/json-tree-path.util";
import {TestsService} from "../../../service/tests.service";
import FeaturesTreeUtil from "./util/features-tree.util";
import {FeatureService} from "../../../service/feature.service";
import {RootServerTreeNode} from "../../../model/tree/root-server-tree-node.model";

@Injectable()
export class FeaturesTreeService {

    testsJsonTreeModel: JsonTreeModel;

    constructor(private testsService: TestsService,
                private featureService: FeatureService){
    }

    initializeTestsTreeFromServer() {
        this.featureService.getFeatureTree().subscribe(
            (rootNode: RootServerTreeNode) => {
                console.log(rootNode);
                this.testsJsonTreeModel =  FeaturesTreeUtil.mapServerTreeToFeaturesTreeModel(rootNode);
                this.sort();

            }
        )
    }

    copy(pathToCopy: Path, destinationPath: Path) {
        JsonTreePathUtil.copy(this.testsJsonTreeModel, pathToCopy, destinationPath);

        let newParent:FeatureTreeContainerModel = JsonTreePathUtil.getNode(this.testsJsonTreeModel, destinationPath) as FeatureTreeContainerModel;
        newParent.sort();
    }

    sort(): void {
        this.sortChildren(this.testsJsonTreeModel.children);
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

        children.forEach( it => {
            if(it.isContainer()) {
                this.sortChildren((it as FeatureTreeContainerModel).children)
            }
        })
    }
}
