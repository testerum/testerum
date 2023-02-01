import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {ManualTestPlan} from "../../model/manual-test-plan.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTestPlansService} from "../../../service/manual-test-plans.service";
import {RootFeatureNode} from "../../../../../model/feature/tree/root-feature-node.model";
import ManualSelectTestsTreeUtil from "./util/manual-select-tests-tree.util";
import {ManualSelectTestsTreeContainerModel} from "./model/manual-select-tests-tree-container.model";
import {ManualSelectTestsTreeNodeModel} from "./model/manual-select-tests-tree-node.model";
import {ManualSelectTestsTreeComponentService} from "./manual-select-tests-tree.component-service";
import {ManualSelectTestsContainerComponent} from "./container/manual-select-tests-container.component";
import {ManualSelectTestsNodeComponent} from "./container/node/manual-select-tests-node.component";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../../../generic/components/json-tree/util/json-tree-path.util";
import {ManualTreeTest} from "../../model/manual-tree-test.model";

@Component({
    selector: 'manual-select-tests-tree',
    templateUrl: 'manual-select-tests-tree.component.html',
    providers: [ManualSelectTestsTreeComponentService]
})
export class ManualSelectTestsTreeComponent implements OnInit, OnChanges {
    @Input() model: ManualTestPlan;
    @Input() isEditMode: boolean;

    treeModel: JsonTreeModel = ManualSelectTestsTreeUtil.createRootPackage();
    manualSelectTreeComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualSelectTestsTreeContainerModel, ManualSelectTestsContainerComponent)
        .addPair(ManualSelectTestsTreeNodeModel, ManualSelectTestsNodeComponent);

    constructor(private manualExecPlansService: ManualTestPlansService,
                private manualSelectTestsTreeComponentService: ManualSelectTestsTreeComponentService) {
    }

    ngOnInit() {
        this.manualSelectTestsTreeComponentService.isEditMode = this.isEditMode;
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.model.currentValue != changes.model.previousValue) {
            this.initTree()
        }
    }

    private initTree() {
        this.manualExecPlansService.getAllManualTests().subscribe((rootFeatureNode: RootFeatureNode) => {
            ManualSelectTestsTreeUtil.mapFeaturesWithTestsToTreeModel(this.treeModel, rootFeatureNode);
            this.setSelectedPath(this.model.manualTreeTests, this.treeModel.getChildren()[0] as ManualSelectTestsTreeContainerModel);
        });
    }

    private setSelectedPath(manualTreeTests: ManualTreeTest[], rootNode: ManualSelectTestsTreeContainerModel) {
        for (const manualTreeTest of manualTreeTests) {
            this.selectOrAddPath(manualTreeTest, rootNode);
        }
    }

    private selectOrAddPath(manualTreeTest: ManualTreeTest, rootNode: ManualSelectTestsTreeContainerModel) {
        let testContainer = this.getOrCreateContainersForPath(manualTreeTest.path, rootNode);

        let nodeToSelect = testContainer.getChildNodeWithPath(manualTreeTest.path);
        if (nodeToSelect == null) {
            testContainer.children.push(
                this.getStepTreeNode(testContainer, manualTreeTest)
            );
        } else {
            nodeToSelect.setSelected(true);
        }
        testContainer.calculateCheckState();
    }

    private getOrCreateContainersForPath(stepPath: Path, parentContainer: ManualSelectTestsTreeContainerModel) {
        if (!stepPath) {
            return parentContainer;
        }

        let result: ManualSelectTestsTreeContainerModel = parentContainer;
        for (let pathDirectory of stepPath.directories) {
            result = this.getOrCreateContainer(pathDirectory, result)
        }
        return result;
    }

    private getOrCreateContainer(childContainerName: string, parentContainer: ManualSelectTestsTreeContainerModel) {
        let childContainer: ManualSelectTestsTreeContainerModel = JsonTreePathUtil.findNode(childContainerName, parentContainer.children) as ManualSelectTestsTreeContainerModel;

        if(childContainer == null) {
            let childContainerPath = Path.createInstanceFromPath(parentContainer.path);
            childContainerPath.directories.push(childContainerName);

            childContainer = new ManualSelectTestsTreeContainerModel(parentContainer, childContainerName, childContainerPath);
            childContainer.editable = true;
            parentContainer.children.push(childContainer);
        }

        return childContainer;
    }

    private getStepTreeNode(parentContainer: ManualSelectTestsTreeContainerModel, manualTreeTest: ManualTreeTest): ManualSelectTestsTreeNodeModel {
        return new ManualSelectTestsTreeNodeModel(
            parentContainer,
            manualTreeTest.testName,
            manualTreeTest.path,
            true
        );
    }

    getSelectedTests(): ManualTreeTest[] {
        let result: ManualTreeTest[] = [];
        this.collectSelectedTests(result, this.treeModel.getChildren()[0] as ManualSelectTestsTreeContainerModel);

        return result;
    }

    private collectSelectedTests(result: ManualTreeTest[], parentNode: ManualSelectTestsTreeContainerModel) {
        for (const child of parentNode.children) {
            if (child instanceof ManualSelectTestsTreeContainerModel) {
                this.collectSelectedTests(result, child);
            }

            let selectTest = child as ManualSelectTestsTreeNodeModel;
            if (selectTest.isSelected()) {
                let manualTreeTest = new ManualTreeTest();
                manualTreeTest.path = selectTest.path;
                manualTreeTest.testName = selectTest.name;
                result.push(manualTreeTest)
            }
        }
    }
}
