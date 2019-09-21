import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {StringTypeMeta} from "../../../../../model/text/parts/param-meta/string-type.meta";
import {JsonUtil} from "../../../../../utils/json.util";
import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {ObjectTreeModel} from "./interfaces/object-tree.model";
import {EnumTypeMeta} from "../../../../../model/text/parts/param-meta/enum-type.meta";
import {BooleanTypeMeta} from "../../../../../model/text/parts/param-meta/boolean-type.meta";
import {DateUtil} from "../../../../../utils/date.util";
import {StringUtils} from "../../../../../utils/string-utils.util";

export class BooleanObjectTreeModel extends JsonTreeNodeAbstract implements ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: BooleanTypeMeta;

    value: string;

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: BooleanTypeMeta) {
        super(parentContainer);
        this.objectName = objectName;
        this.serverObject = serverObject;
        this.typeMeta = typeMeta;
        this.value = serverObject;
    }

    isEmpty(): boolean {
        return StringUtils.isEmpty(this.value);
    }

    serialize(): string {
        if (this.value == "true") return "true";
        if (this.value == "false") return "false";
        return  JsonUtil.stringify(this.value);
    }
}
