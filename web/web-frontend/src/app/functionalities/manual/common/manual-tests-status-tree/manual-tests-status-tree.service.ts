import {Injectable} from "@angular/core";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualTestsStatusTreeRoot} from "../../plans/model/status-tree/manual-tests-status-tree-root.model";
import {ManualTestsStatusTreeUtil} from "./util/manual-tests-status-tree.util";
import {ManualExecPlansService} from "../../service/manual-exec-plans.service";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";

@Injectable()
export class ManualTestsStatusTreeService {

    filter: ManualTreeStatusFilterModel;
    treeModel: JsonTreeModel = new JsonTreeModel();

    constructor(private manualExecPlanService: ManualExecPlansService) {
    }

    initializeTreeFromServer(planPath: Path, filter: ManualTreeStatusFilterModel) {
        this.manualExecPlanService.getManualTestsStatusTree(planPath, filter).subscribe((manualTestsStatusTreeRoot: ManualTestsStatusTreeRoot) => {
            ManualTestsStatusTreeUtil.mapServerModelToTreeModel(manualTestsStatusTreeRoot, this.treeModel)
        });
    }
}
