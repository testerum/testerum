import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {ManualTestTreeNodeModel} from "./model/manual-test-tree-node.model";
import {ManualTestTreeContainerModel} from "./model/manual-test-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../generic/components/json-tree/util/json-tree-path.util";
import ManualTestsTreeUtil from "./util/manual-tests-tree.util";
import {ManualTestModel} from "../../model/manual-test.model";
import {ManualTestsService} from "../service/manual-tests.service";

@Injectable()
export class ManualTestsTreeService {

    testsJsonTreeModel: JsonTreeModel;
    testModels: Array<ManualTestModel>;

    constructor(private manualTestsService: ManualTestsService){
    }

    initializeTestsTreeFromServer() {
        this.manualTestsService.getTests().subscribe(
            testModels => this.setTestModels(testModels)
        );
    }

    setTestModels(testModels: Array<ManualTestModel>): void {
        this.testModels = testModels;
        this.testsJsonTreeModel =  ManualTestsTreeUtil.mapTestModelToTestJsonTreeModel(testModels);
        this.sort();
    }

    copy(pathToCopy: Path, destinationPath: Path) {
        JsonTreePathUtil.copy(this.testsJsonTreeModel, pathToCopy, destinationPath);

        let newParent:ManualTestTreeContainerModel = JsonTreePathUtil.getNode(this.testsJsonTreeModel, destinationPath) as ManualTestTreeContainerModel;
        newParent.sort();
    }

    sort(): void {
        this.sortChildren(this.testsJsonTreeModel.children);
    }

    private sortChildren(children: Array<JsonTreeNode>) {

        children.sort((left: ManualTestTreeNodeModel, right: ManualTestTreeNodeModel) => {
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
                this.sortChildren((it as ManualTestTreeContainerModel).children)
            }
        })
    }

    getAllTestModelsUnderContainer(model: ManualTestTreeContainerModel): Array<ManualTestModel> {
        let result: Array<ManualTestModel> = [];

        let containerPath: Path = model.path;
        for (let testModel of this.testModels) {
            if(testModel.path.toString().startsWith(containerPath.toString())) {
                result.push(testModel)
            }
        }
        return result;
    }
}
