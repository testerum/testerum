import {Injectable} from '@angular/core';
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeNode} from "../../../generic/components/json-tree/model/json-tree-node.model";
import {TestTreeNodeModel} from "./model/test-tree-node.model";
import {FeatureTreeContainerModel} from "./model/feature-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreePathUtil} from "../../../generic/components/json-tree/util/json-tree-path.util";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import FeaturesTreeUtil from "./util/features-tree.util";

@Injectable()
export class FeaturesTreeService {

    testsJsonTreeModel: JsonTreeModel;
    testModels: Array<TestModel>;

    constructor(private testsService: TestsService){
    }

    initializeTestsTreeFromServer() {
        this.testsService.getTests().subscribe(
            testModels => this.setTestModels(testModels)
        );
    }

    setTestModels(testModels: Array<TestModel>): void {
        this.testModels = testModels;
        this.testsJsonTreeModel =  FeaturesTreeUtil.mapTestModelToTestJsonTreeModel(testModels);
        this.sort();
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

    getAllTestModelsUnderContainer(model: FeatureTreeContainerModel): Array<TestModel> {
        let result: Array<TestModel> = [];

        let containerPath: Path = model.path;
        for (let testModel of this.testModels) {
            if(testModel.path.toString().startsWith(containerPath.toString())) {
                result.push(testModel)
            }
        }
        return result;
    }
}
