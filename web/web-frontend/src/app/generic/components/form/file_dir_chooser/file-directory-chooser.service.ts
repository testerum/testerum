import {EventEmitter, Injectable} from '@angular/core';
import {JsonTreeModel} from "../../json-tree/model/json-tree.model";
import {FileSystemService} from "../../../../service/file-system.service";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {FileDirectoryChooserContainerModel} from "./model/file-directory-chooser-container.model";
import {FileDirectoryChooserContainerComponent} from "./container/file-directory-chooser-container.component";
import {JsonTreeService} from "../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../json-tree/event/selected-json-tree-node-event.model";
import {Observable, Subject} from "rxjs";

@Injectable()
export class FileDirectoryChooserService {

    selectedNodeEmitter: EventEmitter<JsonTreeNodeEventModel> = new EventEmitter<JsonTreeNodeEventModel>();

    jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FileDirectoryChooserContainerModel, FileDirectoryChooserContainerComponent);

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
