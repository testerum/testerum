
import {JsonTreeModel} from "../json-tree.model";
import {JsonTreeNodeSerializable} from "./json-tree-node-serialzable.model";

export class JsonTreeModelSerializable extends JsonTreeModel{

    children:Array<JsonTreeNodeSerializable> = [];

}
