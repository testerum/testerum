import {EventEmitter, Injectable} from "@angular/core";
import {RunnerTreeFilterModel} from "../../../../features/tests-runner/tests-runner-tree/model/filter/runner-tree-filter.model";

@Injectable()
export class ManualTestsStatusTreeService {

    readonly treeFilterObservable: EventEmitter<RunnerTreeFilterModel> = new EventEmitter<RunnerTreeFilterModel>();

}
