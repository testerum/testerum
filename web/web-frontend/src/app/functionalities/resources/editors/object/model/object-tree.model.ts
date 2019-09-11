import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {TextTypeMeta} from "../../../../../model/text/parts/param-meta/text-type.meta";


export abstract class ObjectTreeModel extends JsonTreeNodeAbstract {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: TextTypeMeta;

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: TextTypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;
    }

    abstract serialize(): any;
}
