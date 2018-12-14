import {EventEmitter, Injectable} from '@angular/core';
import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {FileSystemService} from "../../../../service/file-system.service";
import {FileDirectoryChooserContainerModel} from "./model/file-directory-chooser-container.model";
import {JsonTreeService} from "../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../json-tree/event/selected-json-tree-node-event.model";
import {Observable, Subject} from "rxjs";

@Injectable()
export class FileDirectoryChooserService {

    selectedNodeEmitter: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    constructor(private fileSystemService: FileSystemService,
                private jsonTreeService: JsonTreeService){
        jsonTreeService.expendedNodeEmitter.subscribe(
            (item: JsonTreeNodeEventModel) => {
                this.onFileDirectoryChooserNodeExpanded(item)
            }
        );

        jsonTreeService.selectedNodeEmitter.subscribe(
            (item: JsonTreeNodeEventModel) => {
                this.selectedNodeEmitter.emit(item)
            }
        )
    }

    public initializeDirectoryTreeFromServer(): Observable<JsonTreeModel> {
        let responseSubject: Subject<JsonTreeModel> = new Subject<JsonTreeModel>();

        this.fileSystemService.getDirectoryTree("").subscribe(
            (fileDirNode: FileDirectoryChooserContainerModel) => {

                let fileDirectoryChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

                for (let child of fileDirNode.getChildren()) {
                    child.parent = fileDirectoryChooserJsonTreeModel;

                    fileDirectoryChooserJsonTreeModel.children.push(
                        child
                    )
                }

                fileDirectoryChooserJsonTreeModel.getChildren().forEach((child: FileDirectoryChooserContainerModel) => {
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
        let fileDirectoryNode = nodeEvent.treeNode as FileDirectoryChooserContainerModel;
        if(fileDirectoryNode.hasChildren() && fileDirectoryNode.getChildren().length == 0) {
            this.fileSystemService.getDirectoryTree(fileDirectoryNode.absoluteJavaPath).subscribe(
                (fileDirNode: FileDirectoryChooserContainerModel) => {
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
