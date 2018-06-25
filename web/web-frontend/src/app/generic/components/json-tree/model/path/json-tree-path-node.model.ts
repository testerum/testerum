
import {JsonTreeNodeAbstract} from "../json-tree-node.abstract";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeContainer} from "../json-tree-container.model";
import {JsonTreePathContainer} from "./json-tree-path-container.model";

export class JsonTreePathNode extends JsonTreeNodeAbstract {

    name: string;
    path: Path;
    parentContainer: JsonTreePathContainer;

    constructor(parentContainer: JsonTreePathContainer, path?: Path) {
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

    getParent(): JsonTreePathContainer {
        return this.parentContainer;
    }


}
