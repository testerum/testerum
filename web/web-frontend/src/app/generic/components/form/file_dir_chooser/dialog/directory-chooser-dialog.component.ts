import {Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {FileDirectoryChooserService} from "../file-directory-chooser.service";
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {FileDirectoryChooserContainerModel} from "../model/file-directory-chooser-container.model";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";
import {JsonTreeContainerEditorEvent} from "../../../json-tree/container-editor/model/json-tree-container-editor.event";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {FileSystemService} from "../../../../../service/file-system.service";
import {FileSystemDirectory} from "../../../../../model/file/file-system-directory.model";
import {ErrorService} from "../../../../../service/error.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
    moduleId: module.id,
    selector: 'directory-chooser-dialog',
    templateUrl: 'directory-chooser-dialog.component.html',
    styleUrls: ['directory-chooser-dialog.component.scss']
})
export class DirectoryChooserDialogComponent implements OnInit, OnDestroy {

    @ViewChild("infoModal") infoModal:ModalDirective;

    selectedPath: string;
    selectedNode: FileDirectoryChooserContainerModel;
    private selectedNodeSubscriber: any;
    @Output() nodeSelected: EventEmitter<string> = new EventEmitter<string>();
    fileDirectoryChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

    fileDirectoryChooserService: FileDirectoryChooserService;
    constructor(fileDirectoryChooserService: FileDirectoryChooserService,
                private fileSystemService: FileSystemService,
                private jsonTreeService: JsonTreeService,
                private errorService: ErrorService) {
        this.fileDirectoryChooserService = fileDirectoryChooserService;
    }

    ngOnInit() {
        this.fileDirectoryChooserService.initializeDirectoryTreeFromServer().subscribe(
            (dirTree: JsonTreeModel) => {
                this.fileDirectoryChooserJsonTreeModel.children.length = 0;

                for (let dirTreeChild of dirTree.children) {
                    this.fileDirectoryChooserJsonTreeModel.children.push(
                        dirTreeChild
                    )
                }
            }
        );

        this.selectedNodeSubscriber = this.fileDirectoryChooserService.selectedNodeEmitter.subscribe (
            (item: JsonTreeNodeEventModel) => {
                let selectedNode = item.treeNode as FileDirectoryChooserContainerModel;
                if ((selectedNode).absoluteJavaPath) {
                    this.selectedNode = selectedNode;
                    this.selectedPath = selectedNode.absoluteJavaPath
                }
            }
        );
    }


    ngOnDestroy(): void {
        this.selectedNodeSubscriber.unsubscribe();
    }

    show(): void {
        this.infoModal.show();
    }

    close(): void {
        this.infoModal.hide();
    }

    selectDirectory(): void {
        this.nodeSelected.emit(this.selectedPath);
        this.close();
    }

    cancel(): void {
        this.close();
    }
    createDirectory(): void {
        if(!this.selectedPath) {
            return
        }

        let childrenContainersName = this.getChildrenContanersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {

                this.fileSystemService.createFileSystemDirectory (this.selectedNode.absoluteJavaPath, createEvent.newName).subscribe(
                    (newFileSystemDirectory: FileSystemDirectory) => {
                        let newContainer = new FileDirectoryChooserContainerModel(
                            this.selectedNode,
                            newFileSystemDirectory.name,
                            newFileSystemDirectory.absoluteJavaPath,
                            false
                        );
                        this.selectedNode.getChildren().push(newContainer);
                        this.selectedNode.sort();
                    },
                    (error: HttpErrorResponse) => {
                        this.errorService.handleHttpResponseException(error)
                    }
                );
            }
        )
    }

    private getChildrenContanersName(): Array<string> {
        let childrenContainersName: Array<string> = [];
        for (const child of this.selectedNode.getChildren()) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }
}
