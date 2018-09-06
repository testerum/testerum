import {EventEmitter, Injectable} from "@angular/core";
import {RunnerTreeFilterModel} from "../../../../features/tests-runner/tests-runner-tree/model/filter/runner-tree-filter.model";
import {ManualTreeStatusFilterModel} from "./model/filter/manual-tree-status-filter.model";

@Injectable()
export class ManualTestsStatusTreeService {

    readonly treeFilterObservable: EventEmitter<ManualTreeStatusFilterModel> = new EventEmitter<ManualTreeStatusFilterModel>();

}
