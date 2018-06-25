
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {ManualTestExeModel} from "../../../runner/model/manual-test-exe.model";
import {ManualTestsTreeExecutorContainerModel} from "./manual-tests-tree-executor-container.model";


export class ManualTestsTreeExecutorNodeModel extends JsonTreePathNode implements Comparable<ManualTestsTreeExecutorNodeModel> {

    name: string;
    path: Path;
    isSelected: boolean;
    id: string; //TODO: to be removed when we have resources as file
    payload: ManualTestExeModel;

    constructor(id: string, name: string,  path: Path, payload: ManualTestExeModel, isSelected: boolean = false, parentContainer: ManualTestsTreeExecutorContainerModel) {
        super(parentContainer, path);
        this.id = id;
        this.name = name;
        this.payload = payload;
        this.isSelected = isSelected;
    }

    compareTo(other: ManualTestsTreeExecutorNodeModel): number {

        let thisName = this.name;
        let otherName = other.name;

        if(thisName.toUpperCase() < otherName.toUpperCase()) return -1;
        if(thisName.toUpperCase() > otherName.toUpperCase()) return 1;

        return 0;
    }
}
