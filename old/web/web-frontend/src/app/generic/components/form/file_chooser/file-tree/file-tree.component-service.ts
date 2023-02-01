import {Injectable} from "@angular/core";
import {FileTreeNode} from "./model/file-tree-node.model";

@Injectable()
export class FileTreeComponentService {
    selectedNode: FileTreeNode;
    isTesterumProjectChooser: boolean = false;
}

