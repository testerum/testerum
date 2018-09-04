
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {OldManualTestStatus} from "../../../model/enums/manual-test-status.enum";
import {ManualTestsTreeExecutorNodeModel} from "./manual-tests-tree-executor-node.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class ManualTestsTreeExecutorContainerModel extends ManualTestsTreeExecutorNodeModel implements JsonTreeContainer, Comparable<ManualTestsTreeExecutorContainerModel> {

    children: Array<ManualTestsTreeExecutorNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;

    testsState: OldManualTestStatus = OldManualTestStatus.NOT_EXECUTED;

    totalTests: number = 0;
    notExecutedTests: number = 0;
    passedTests: number = 0;
    failedTests: number = 0;
    blockedTests: number = 0;
    notApplicableTests: number = 0;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(name: string, path: Path = null, testsState: OldManualTestStatus = OldManualTestStatus.NOT_EXECUTED, parentContainer: ManualTestsTreeExecutorContainerModel) {
        super(null, name, path, null, false, parentContainer);

        this.testsState = testsState;
        this.jsonTreeNodeState.showChildren = true;
    }

    getChildren(): Array<ManualTestsTreeExecutorNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: ManualTestsTreeExecutorNodeModel, right: ManualTestsTreeExecutorNodeModel) => {
            if(!left.isContainer() && right.isContainer()) {
                return 1;
            }

            return left.compareTo(right);
        });
    }

    compareTo(other: ManualTestsTreeExecutorNodeModel): number {
        if(!other.isContainer()) {
            return -1;
        }

        if(this.name.toUpperCase() < other.name.toUpperCase()) return -1;
        if(this.name.toUpperCase() > other.name.toUpperCase()) return 1;

        return 0;
    }

    isContainer(): boolean {
        return true;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
