import {EventEmitter, Injectable} from '@angular/core';
import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {FileSystemService} from "../../../../service/file-system.service";
import {FileTreeContainer} from "./file-tree/model/file-tree.container";
import {JsonTreeService} from "../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../json-tree/event/selected-json-tree-node-event.model";
import {Observable, Subject} from "rxjs";
import {FileTreeNode} from "./file-tree/model/file-tree-node.model";

@Injectable()
export class FileChooserService {

    showFiles: boolean = false;

    constructor(private fileSystemService: FileSystemService,
                private jsonTreeService: JsonTreeService){
        jsonTreeService.expendedNodeEmitter.subscribe(
            (item: JsonTreeNodeEventModel) => {
                this.onFileDirectoryChooserNodeExpanded(item)
            }
        );
    }

    public initializeDirectoryTreeFromServer(showFiles: boolean): Observable<JsonTreeModel> {
        let responseSubject: Subject<JsonTreeModel> = new Subject<JsonTreeModel>();

        this.fileSystemService.getDirectoryTree("", showFiles).subscribe(
            (fileDirNode: FileTreeContainer) => {

                let fileDirectoryChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

                for (let child of fileDirNode.getChildren()) {
                    child.parentContainer = fileDirectoryChooserJsonTreeModel;

                    fileDirectoryChooserJsonTreeModel.children.push(
                        child
                    )
                }

                fileDirectoryChooserJsonTreeModel.getChildren().forEach((child: FileTreeNode) => {
                    if (child instanceof FileTreeContainer) {
                        child.getNodeState().showChildren = true;
                    }
                });

                responseSubject.next(
                    fileDirectoryChooserJsonTreeModel
                )
            }
        );
        return responseSubject.asObservable();
    }

    private onFileDirectoryChooserNodeExpanded(nodeEvent: JsonTreeNodeEventModel) {
        let fileDirectoryNode = nodeEvent.treeNode as FileTreeContainer;
        if(fileDirectoryNode.hasChildren() && fileDirectoryNode.getChildren().length == 0) {
            this.fileSystemService.getDirectoryTree(fileDirectoryNode.absoluteJavaPath, this.showFiles).subscribe(
                (fileDirNode: FileTreeContainer) => {
                    fileDirectoryNode.getChildren().length = 0;
                    for (let child of fileDirNode.getChildren()) {
                        child.parentContainer = fileDirectoryNode;
                        fileDirectoryNode.getChildren().push(
                            child
                        )
                    }
                }
            );
        }
    }
}
