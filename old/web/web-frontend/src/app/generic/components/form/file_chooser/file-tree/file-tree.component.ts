import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FileTreeComponentService} from "./file-tree.component-service";
import {FileTreeContainer} from "./model/file-tree.container";
import {ModelComponentMapping} from "../../../../../model/infrastructure/model-component-mapping.model";
import {FileTreeContainerComponent} from "./nodes/container/file-tree-container.component";
import {Observable, Subject, Subscription} from "rxjs";
import {FileChooserService} from "../file-chooser.service";
import {FileSystemService} from "../../../../../service/file-system.service";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {ErrorHttpInterceptor} from "../../../../../service/interceptors/error.http-interceptor";
import {JsonTreeContainerEditorEvent} from "../../../json-tree/container-editor/model/json-tree-container-editor.event";
import {FileSystemDirectory} from "../../../../../model/file/file-system-directory.model";
import {HttpErrorResponse} from "@angular/common/http";
import {JsonTreeModel} from "../../../json-tree/model/json-tree.model";
import {FileTreeNode} from "./model/file-tree-node.model";
import {FileTreeNodeComponent} from "./nodes/node/file-tree-node.component";

@Component({
    selector: 'file-tree',
    templateUrl: './file-tree.component.html',
    providers: [FileTreeComponentService]
})
export class FileTreeComponent  implements OnInit, OnDestroy {

    @Input() isTesterumProjectChooser: boolean = false;
    @Input() showFiles: boolean = false;

    fileChooserJsonTreeModel: JsonTreeModel = new JsonTreeModel();

    jsonModelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(FileTreeContainer, FileTreeContainerComponent)
        .addPair(FileTreeNode, FileTreeNodeComponent);

    private initializeTreeFromServerSubscription: Subscription;

    constructor(public fileChooserService: FileChooserService,
                private fileTreeComponentService: FileTreeComponentService,
                private fileSystemService: FileSystemService,
                private jsonTreeService: JsonTreeService,
                private errorService: ErrorHttpInterceptor) {
    }

    ngOnInit() {
        this.fileTreeComponentService.isTesterumProjectChooser = this.isTesterumProjectChooser;

        this.fileChooserService.showFiles = this.showFiles;
        this.initializeTreeFromServerSubscription = this.fileChooserService.initializeDirectoryTreeFromServer(this.showFiles).subscribe(
            (dirTree: JsonTreeModel) => {
                this.fileChooserJsonTreeModel.children.length = 0;

                for (let dirTreeChild of dirTree.children) {
                    this.fileChooserJsonTreeModel.children.push(
                        dirTreeChild
                    )
                }
            }
        );
    }

    ngOnDestroy(): void {
        if (this.initializeTreeFromServerSubscription) {
            this.initializeTreeFromServerSubscription.unsubscribe();
        }
    }

    getSelectedPathAsString(): string {
        if (!this.getSelectedNode()) {
            return null;
        }
        return this.getSelectedNode().absoluteJavaPath;
    }

    getSelectedNode(): FileTreeNode {
        return this.fileTreeComponentService.selectedNode;
    }

    getSelectedDir(): FileTreeContainer {
        if (this.fileTreeComponentService.selectedNode instanceof FileTreeContainer) {
            return this.fileTreeComponentService.selectedNode;
        }
        return null;
    }

    createDirectory(): Observable<FileTreeContainer> {
        let subject = new Subject<FileTreeContainer>();
        if (!this.getSelectedNode()) {
            return subject;
        }

        let childrenContainersName = this.getChildrenContanersName();

        this.jsonTreeService.triggerCreateContainerAction(childrenContainersName).subscribe(
            (createEvent: JsonTreeContainerEditorEvent) => {

                this.fileSystemService.createFileSystemDirectory(this.getSelectedPathAsString(), createEvent.newName).subscribe(
                    (newFileSystemDirectory: FileSystemDirectory) => {
                        let newContainer = new FileTreeContainer(
                            this.getSelectedDir(),
                            newFileSystemDirectory.name,
                            newFileSystemDirectory.absoluteJavaPath,
                            newFileSystemDirectory.isProject,
                            newFileSystemDirectory.canCreateChild,
                            false
                        );
                        this.getSelectedDir().getChildren().push(newContainer);
                        this.getSelectedDir().sort();

                        subject.next(newContainer);
                        subject.complete();
                    },
                    (error: HttpErrorResponse) => {
                        this.errorService.handleHttpResponseException(error);
                        subject.complete();
                    }
                );
            }
        );

        return subject;
    }

    setSelectedDirectory(newSelectedDirectory: FileTreeContainer) {
        this.fileTreeComponentService.selectedNode = newSelectedDirectory;
        this.jsonTreeService.setSelectedNode(newSelectedDirectory);
    }

    private getChildrenContanersName(): Array<string> {
        let childrenContainersName: Array<string> = [];
        for (const child of this.getSelectedDir().getChildren()) {
            if (child.isContainer()) {
                childrenContainersName.push(child.name)
            }
        }
        return childrenContainersName;
    }
}
