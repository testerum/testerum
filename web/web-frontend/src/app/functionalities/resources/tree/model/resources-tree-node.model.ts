import {IdUtils} from "../../../../utils/id.util";
import {ResourceType} from "./type/resource-type.model";
import {TreeNodeModel} from "../../../../model/infrastructure/tree-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {JsonTreeNode} from "../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeNodeAbstract} from "../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {ResourcesTreeContainer} from "./resources-tree-container.model";

export class ResourcesTreeNode extends JsonTreePathNode {

    name: string;
    readonly path: Path;
    readonly resourceType: ResourceType;

    constructor(parentContainer: ResourcesTreeContainer, resourceType: ResourceType, path?: Path) {
        super(parentContainer, path);
        if(!path) {
            this.path = Path.createInstanceOfEmptyPath();
            this.name = resourceType.rootFilePath.getLastPathPart()
        }

        this.resourceType = resourceType;
    }
}
