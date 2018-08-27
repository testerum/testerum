import {JsonTreePathNode} from "../../../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Comparable} from "../../../../../../model/infrastructure/comparable.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {JsonTreePathContainer} from "../../../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class SelectTestsTreeNodeModel extends JsonTreePathNode implements Comparable<SelectTestsTreeNodeModel> {

    name: string;
    path: Path;
    isSelected: boolean;

    constructor(parentContainer: JsonTreePathContainer, name: string,  path: Path, isSelected: boolean = false) {
        super(parentContainer, path);
        this.name = name;
        this.isSelected = isSelected;
    }

    compareTo(other: SelectTestsTreeNodeModel): number {

        let thisName = this.name;
        let otherName = other.name;

        if(thisName.toUpperCase() < otherName.toUpperCase()) return -1;
        if(thisName.toUpperCase() > otherName.toUpperCase()) return 1;

        return 0;
    }
}
