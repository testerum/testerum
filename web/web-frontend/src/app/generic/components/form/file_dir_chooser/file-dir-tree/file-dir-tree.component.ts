import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FileDirTreeComponentService} from "./file-dir-tree.component-service";
import {FileDirTreeContainerModel} from "./model/file-dir-tree-container.model";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {FileDirTreeContainerComponent} from "./nodes/container/file-dir-tree-container.component";
import {Subscription} from "rxjs";
import {FileDirChooserService} from "../file-dir-chooser.service";
import {FileSystemService} from "../../../../../service/file-system.service";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {ErrorHttpInterceptor} from "../../../../../service/interceptors/error.http-interceptor";
import {JsonTreeContainerEditorEvent} from "../../../json-tree/container-editor/model/json-tree-container-editor.event";
import {FileSystemDirectory} from "../../../../../model/file/file-system-directory.model";
import {HttpErrorResponse} from "@angular/common/http";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";

@Component({
    selector: 'file-dir-tree',
    templateUrl: './file-dir-tree.component.html',
    providers: [FileDirTreeComponentService]
})
export class FileDirTreeComponent  implements OnInit, OnDestroy {

    @Input() isTesterumProjectChooser: boolean = false;
    fileDirectoryChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

    jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FileDirTreeContainerModel, FileDirTreeContainerComponent);

    private initializeDirectoryTreeFromServerSubscription: Subscription;

    constructor(public fileDirectoryChooserService: FileDirChooserService,
                private fileDirTreeComponentService: FileDirTreeComponentService,
                private fileSystemService: FileSystemService,
                private jsonTreeService: JsonTreeService,
                private errorService: ErrorHttpInterceptor) {
    }


    ngOnInit() {
        this.fileDirTreeComponentService.isTesterumProjectChooser = this.isTesterumProjectChooser;

        this.initializeDirectoryTreeFromServerSubscription = this.fileDirectoryChooserService.initializeDirectoryTreeFromServer().subscribe(
            (dirTree: JsonTreeModel) => {
                this.fileDirectoryChooserJsonTreeModel.children.length = 0;

                for (let dirTreeChild of dirTree.children) {
                    this.fileDirectoryChooserJsonTreeModel.children.push(
                        dirTreeChild
                    )
                }
            }
        );
    }

    ngOnDestroy(): void {
        if (this.initializeDirectoryTreeFromServerSubscription) {
            this.initializeDirectoryTreeFromServerSubscription.unsubscribe();
        }
    }

    getSelectedPathAsString(): string {
        if (!this.getSelectedNode()) {
            return null;
        }
        return this.getSelectedNode().absoluteJavaPath;
    }

    getSelectedNode(): FileDirTreeContainerModel {
        return this.fileDirTreeComponentService.selectedNode;
    }

    createDirectory(): void {
        if (!this.getSelectedNode()) {
            return
        }

        let childrenContainersName = this.getChildrenContanersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {

                this.fileSystemService.createFileSystemDirectory(this.getSelectedPathAsString(), createEvent.newName).subscribe(
                    (newFileSystemDirectory: FileSystemDirectory) => {
                        let newContainer = new FileDirTreeContainerModel(
                            this.getSelectedNode(),
                            newFileSystemDirectory.name,
                            newFileSystemDirectory.absoluteJavaPath,
                            newFileSystemDirectory.isProject,
                            newFileSystemDirectory.canCreateChild,
                            false
                        );
                        this.getSelectedNode().getChildren().push(newContainer);
                        this.getSelectedNode().sort();
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
        for (const child of this.getSelectedNode().getChildren()) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }
}
