import {JsonTreeModel} from "../../../../../../../generic/components/json-tree/model/json-tree.model";
import {RootFeatureNode} from "../../../../../../../model/feature/tree/root-feature-node.model";
import {FeatureTreeContainerModel} from "../../../../../../features/features-tree/model/feature-tree-container.model";
import {ContainerFeatureNode} from "../../../../../../../model/feature/tree/container-feature-node.model";
import {FeatureFeatureNode} from "../../../../../../../model/feature/tree/feature-feature-node.model";
import {TestFeatureNode} from "../../../../../../../model/feature/tree/test-feature-node.model";
import {TestTreeNodeModel} from "../../../../../../features/features-tree/model/test-tree-node.model";
import {RunnerConfigTestTreeContainerModel} from "../model/runner-config-test-tree-container.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {RunnerConfigTestTreeNodeModel} from "../model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeRootModel} from "../model/runner-config-test-tree-root.model";
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";

export class RunnerConfigTestTreeUtil {

    static mapServerModelToTreeModel(serverRootNode: RootFeatureNode, treeModel: JsonTreeModel): JsonTreeModel {

        treeModel.children.length = 0;

        let rootPackage: RunnerConfigTestTreeRootModel = new RunnerConfigTestTreeRootModel(
            treeModel,
            serverRootNode.path,
            serverRootNode.name,
            ManualTestStatus.NOT_EXECUTED
        );

        RunnerConfigTestTreeUtil.mapServerNodeChildrenToTreeNode(serverRootNode, rootPackage);

        treeModel.children = [rootPackage];
        rootPackage.parentContainer = treeModel;

        RunnerConfigTestTreeUtil.sort(treeModel);

        return treeModel;
    }

    private static mapServerNodeChildrenToTreeNode(serverContainerNode: ContainerFeatureNode, uiContainerNode: RunnerConfigTestTreeContainerModel) {
        for (const serverNode of serverContainerNode.children) {

            if (serverNode instanceof FeatureFeatureNode) {
                let uiFeatureNode = new RunnerConfigTestTreeContainerModel(uiContainerNode, serverNode.path, serverNode.name, ManualTestStatus.NOT_EXECUTED);
                RunnerConfigTestTreeUtil.mapServerNodeChildrenToTreeNode(serverNode, uiFeatureNode);
                uiContainerNode.children.push(uiFeatureNode);
            }

            if (serverNode instanceof TestFeatureNode) {
                let uiTestNode = new RunnerConfigTestTreeNodeModel(uiContainerNode, serverNode.path, serverNode.name, ManualTestStatus.NOT_EXECUTED);
                uiContainerNode.children.push(uiTestNode)
            }
        }
    }

    private static sort(testsJsonTreeModel: JsonTreeModel): void {
        this.sortChildren(testsJsonTreeModel.children);
    }

    private static sortChildren(children: Array<JsonTreeNode>) {

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
}
