import {JsonTreeNode} from "../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeContainerAbstract} from "../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {ResultsTreeNodeModel} from "./results-tree-node.model";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";

export class ResultsTreeContainerModel extends JsonTreeContainerAbstract {

    name: string;
    resultFiles: Array<ResultsTreeNodeModel> = [];

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    getChildren(): Array<JsonTreeNode> {
        return this.resultFiles;
    }
}
