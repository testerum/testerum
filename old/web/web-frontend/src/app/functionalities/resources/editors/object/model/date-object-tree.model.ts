import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonUtil} from "../../../../../utils/json.util";
import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {ObjectTreeModel} from "./interfaces/object-tree.model";
import {StringUtils} from "../../../../../utils/string-utils.util";
import {TypeMeta} from "../../../../../model/text/parts/param-meta/type-meta.model";

export class DateObjectTreeModel extends JsonTreeNodeAbstract implements ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: TypeMeta;

    value: string;

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: TypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;
        this.value = serverObject
    }

    isEmpty(): boolean {
        return StringUtils.isEmpty(this.value);
    }

    serialize(): string {
        return  JsonUtil.stringify(this.value);
    }
}
