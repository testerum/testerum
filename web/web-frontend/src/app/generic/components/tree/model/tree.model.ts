
import {TreeContainerModel} from "../../../../model/infrastructure/tree-container.model";
import {TreeNodeModel} from "../../../../model/infrastructure/tree-node.model";

export class TreeModel<T extends TreeContainerModel<any, any>, K extends TreeNodeModel> {
    childContainers:Array<T> = [];
    childNodes:Array<K> = [];
}