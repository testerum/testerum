import {Injectable} from "@angular/core";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualTestsStatusTreeRoot} from "../../plans/model/status-tree/manual-tests-status-tree-root.model";
import {ManualTestsStatusTreeUtil} from "./util/manual-tests-status-tree.util";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeExpandUtil} from "../../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeService} from "../../../../generic/components/json-tree/json-tree.service";

@Injectable()
export class ManualTestsStatusTreeService {

    treeModel: JsonTreeModel = new JsonTreeModel();

    constructor(private jsonTreeService: JsonTreeService,
                private manualExecPlanService: ManualTestPlansService) {
    }

    initializeTreeFromServer(planPath: Path, testPath: Path, filter: ManualTreeStatusFilterModel) {
        this.manualExecPlanService.getManualTestsStatusTree(planPath, filter).subscribe((manualTestsStatusTreeRoot: ManualTestsStatusTreeRoot) => {
            ManualTestsStatusTreeUtil.mapServerModelToTreeModel(manualTestsStatusTreeRoot, this.treeModel);
            this.selectNodeAtPath(testPath);
        });
    }

    selectNodeAtPath(path: Path) {
        if (this.treeModel) {
            let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.treeModel, path);
            this.jsonTreeService.setSelectedNode(selectedNode);
        }
    }
}
