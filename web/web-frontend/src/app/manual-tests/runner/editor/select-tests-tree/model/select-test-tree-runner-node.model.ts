
import {JsonTreePathNode} from "../../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {Comparable} from "../../../../../model/infrastructure/comparable.model";
import {ManualTestModel} from "../../../../model/manual-test.model";
import {ManualTestExeModel} from "../../../model/manual-test-exe.model";
import {SelectTestTreeRunnerContainerModel} from "./select-test-tree-runner-container.model";


export class SelectTestTreeRunnerNodeModel extends JsonTreePathNode implements Comparable<SelectTestTreeRunnerNodeModel> {

    name: string;
    path: Path;
    isSelected: boolean;
    id: string; //TODO: to be removed when we have resources as file
    payload: ManualTestExeModel;

    constructor(parentContainer: SelectTestTreeRunnerContainerModel, id: string, name: string,  path: Path, payload: ManualTestExeModel, isSelected: boolean = false) {
        super(parentContainer, path);
        this.id = id;
        this.name = name;
        this.payload = payload;
        this.isSelected = isSelected;
    }

    compareTo(other: SelectTestTreeRunnerNodeModel): number {

        let thisName = this.name;
        let otherName = other.name;

        if(thisName.toUpperCase() < otherName.toUpperCase()) return -1;
        if(thisName.toUpperCase() > otherName.toUpperCase()) return 1;

        return 0;
    }
}
