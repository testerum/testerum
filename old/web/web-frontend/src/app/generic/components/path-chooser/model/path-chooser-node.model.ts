
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {PathChooserContainerModel} from "./path-chooser-container.model";
import {JsonTreeContainer} from "../../json-tree/model/json-tree-container.model";
import {JsonTreePathContainer} from "../../json-tree/model/path/json-tree-path-container.model";
import {JsonTreeNode} from "../../json-tree/model/json-tree-node.model";
import {JsonTreeNodeAbstract} from "../../json-tree/model/json-tree-node.abstract";

export class PathChooserNodeModel extends JsonTreeNodeAbstract {

    name: string;
    path: Path;
    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer, path: Path) {
        super(parentContainer);


        if (path != null) {
            if (path.fileName) {
                this.name = path.fileName;
            } else {
                this.name = path.getLastPathPart();
            }
            this.path = path;
        }
    }
}
