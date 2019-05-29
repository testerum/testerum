import {JsonTreeModel} from "../../../../../../../generic/components/json-tree/model/json-tree.model";
import {RootFeatureNode} from "../../../../../../../model/feature/tree/root-feature-node.model";
import {FeatureTreeContainerModel} from "../../../../../../features/features-tree/model/feature-tree-container.model";
import {ContainerFeatureNode} from "../../../../../../../model/feature/tree/container-feature-node.model";
import {FeatureFeatureNode} from "../../../../../../../model/feature/tree/feature-feature-node.model";
import {TestFeatureNode} from "../../../../../../../model/feature/tree/test-feature-node.model";
import {TestTreeNodeModel} from "../../../../../../features/features-tree/model/test-tree-node.model";
import {RunConfigTestTreeContainerModel} from "../model/run-config-test-tree-container.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {RunConfigTestTreeNodeModel} from "../model/run-config-test-tree-node.model";
import {RunConfigTestTreeRootModel} from "../model/run-config-test-tree-root.model";
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {RunConfigTestTreeNodeStatusEnum} from "../model/enum/run-config-test-tree-node-status.enum";

export class RunConfigTestTreeUtil {

    static mapServerModelToTreeModel(serverRootNode: RootFeatureNode, treeModel: JsonTreeModel): JsonTreeModel {

        treeModel.children.length = 0;

        let rootPackage: RunConfigTestTreeRootModel = new RunConfigTestTreeRootModel(
            treeModel,
            serverRootNode.path,
            serverRootNode.name,
            RunConfigTestTreeNodeStatusEnum.NOT_SELECTED
        );

        RunConfigTestTreeUtil.mapServerNodeChildrenToTreeNode(serverRootNode, rootPackage);

        treeModel.children = [rootPackage];
        rootPackage.parentContainer = treeModel;

        RunConfigTestTreeUtil.sort(treeModel);

        return treeModel;
    }

    private static mapServerNodeChildrenToTreeNode(serverContainerNode: ContainerFeatureNode, uiContainerNode: RunConfigTestTreeContainerModel) {
        for (const serverNode of serverContainerNode.children) {

            if (serverNode instanceof FeatureFeatureNode) {
                let uiFeatureNode = new RunConfigTestTreeContainerModel(uiContainerNode, serverNode.path, serverNode.name, RunConfigTestTreeNodeStatusEnum.NOT_SELECTED);
                RunConfigTestTreeUtil.mapServerNodeChildrenToTreeNode(serverNode, uiFeatureNode);
                uiContainerNode.children.push(uiFeatureNode);
            }

            if (serverNode instanceof TestFeatureNode) {
                let uiTestNode = new RunConfigTestTreeNodeModel(uiContainerNode, serverNode.path, serverNode.name, RunConfigTestTreeNodeStatusEnum.NOT_SELECTED);
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
