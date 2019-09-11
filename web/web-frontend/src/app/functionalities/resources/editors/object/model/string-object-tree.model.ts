import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {TextTypeMeta} from "../../../../../model/text/parts/param-meta/text-type.meta";
import {ObjectTreeModel} from "./object-tree.model";

export class StringObjectTreeModel extends ObjectTreeModel {

    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;

    objectName: string;
    serverObject: any;
    typeMeta: TextTypeMeta;

    value: string;

    constructor(parentContainer: JsonTreeContainer, objectName: string, serverObject: any, typeMeta: TextTypeMeta) {
        super(parentContainer, objectName, serverObject, typeMeta);
        this.value = serverObject;
    }

    serialize(): any {
        return this.value;
    }
}
