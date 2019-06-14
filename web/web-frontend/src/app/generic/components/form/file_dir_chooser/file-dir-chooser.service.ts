import {EventEmitter, Injectable} from '@angular/core';
import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {FileSystemService} from "../../../../service/file-system.service";
import {FileDirTreeContainerModel} from "./file-dir-tree/model/file-dir-tree-container.model";
import {JsonTreeService} from "../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../json-tree/event/selected-json-tree-node-event.model";
import {Observable, Subject} from "rxjs";

@Injectable()
export class FileDirChooserService {

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
            (fileDirNode: FileDirTreeContainerModel) => {

                let fileDirectoryChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

                for (let child of fileDirNode.getChildren()) {
                    child.parent = fileDirectoryChooserJsonTreeModel;

                    fileDirectoryChooserJsonTreeModel.children.push(
                        child
                    )
                }

                fileDirectoryChooserJsonTreeModel.getChildren().forEach((child: FileDirTreeContainerModel) => {
                    child.getNodeState().showChildren = true;
                });

                responseSubject.next(
                    fileDirectoryChooserJsonTreeModel
                )
            }
        );
        return responseSubject.asObservable();
    }

    private onFileDirectoryChooserNodeExpanded(nodeEvent: JsonTreeNodeEventModel) {
        let fileDirectoryNode = nodeEvent.treeNode as FileDirTreeContainerModel;
        if(fileDirectoryNode.hasChildren() && fileDirectoryNode.getChildren().length == 0) {
            this.fileSystemService.getDirectoryTree(fileDirectoryNode.absoluteJavaPath, this.showFiles).subscribe(
                (fileDirNode: FileDirTreeContainerModel) => {
                    for (let child of fileDirNode.getChildren()) {
                        child.parent = fileDirectoryNode;
                        fileDirectoryNode.getChildren().push(
                            child
                        )
                    }
                }
            );
        }
    }
}
