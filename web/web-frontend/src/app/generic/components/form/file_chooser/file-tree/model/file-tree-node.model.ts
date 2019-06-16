import {JsonTreeContainer} from "../../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreeNode} from "../../../../json-tree/model/json-tree-node.model";
import {FileTreeContainer} from "./file-tree.container";
import {JsonTreeNodeAbstract} from "../../../../json-tree/model/json-tree-node.abstract";
import {Arg} from "../../../../../../model/arg/arg.model";
import {ParamStepPatternPart} from "../../../../../../model/text/parts/param-step-pattern-part.model";

export class FileTreeNode extends JsonTreeNodeAbstract {

    parentContainer: JsonTreeContainer;
    name: string;
    absoluteJavaPath: string;

    constructor(parentContainer: JsonTreeContainer, name: string, absoluteJavaPath: string) {
        super(parentContainer);

        this.name = name;
        this.absoluteJavaPath = absoluteJavaPath;
    }
}
