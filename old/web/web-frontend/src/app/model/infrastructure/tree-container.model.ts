
import {TreeNodeModel} from "./tree-node.model";
export interface TreeContainerModel<T extends TreeContainerModel<any, any>, K extends TreeNodeModel>{

    name:string;
    getChildContainers():Array<T>;
    getChildNodes():Array<K>;

}