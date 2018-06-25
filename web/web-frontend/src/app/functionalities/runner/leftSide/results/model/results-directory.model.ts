import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {ResultFile} from "./result-file.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";

export class ResultsDirectory extends JsonTreeContainerAbstract {

    name: string;
    resultFiles: Array<ResultFile> = [];

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    getChildren(): Array<JsonTreeNode> {
        return this.resultFiles;
    }
}
