import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {FileChooserModalService} from "./file-chooser-modal.service";
import {FileTreeComponent} from "../file-tree/file-tree.component";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {Subscription} from "rxjs";
import {FileSystemService} from "../../../../../service/file-system.service";
import {PathInfo} from "../../../../../model/infrastructure/path/path-info.model";
import {FileTreeContainer} from "../file-tree/model/file-tree.container";
import {FileTreeComponentService} from "../file-tree/file-tree.component-service";

@Component({
    moduleId: module.id,
    selector: 'file-dir-chooser-modal',
    templateUrl: 'file-chooser-modal.component.html',
    styleUrls: ['file-chooser-modal.component.scss']
})
export class FileChooserModalComponent implements OnInit, AfterViewInit, OnDestroy {

    @Input() showFiles: boolean;

    @ViewChild(FileTreeComponent) fileTreeComponent: FileTreeComponent;

    @ViewChild("infoModal") modal: ModalDirective;
    directoryChooserDialogService: FileChooserModalService;

    isTesterumProjectChooser: boolean = false;

    selectedPathAsString: string;
    isValidSelectedPath: boolean = false;

    private jsonTreeSelectedNodeSubscription: Subscription;
    private createDirectorySubscription: Subscription;
    constructor(private jsonTreeService: JsonTreeService,
                private fileSystemService: FileSystemService) {
    }

    ngOnInit(): void {
        this.jsonTreeSelectedNodeSubscription = this.jsonTreeService.selectedNodeEmitter.subscribe( (selectedNodeEvent: JsonTreeNodeEventModel) => {
            this.selectedPathAsString = (selectedNodeEvent.treeNode as FileTreeContainer).absoluteJavaPath;
            this.checkIfSelectedPathIsValid();
        })
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.directoryChooserDialogService.clearModal()
        })
    }

    ngOnDestroy(): void {
        if (this.jsonTreeSelectedNodeSubscription != null) this.jsonTreeSelectedNodeSubscription.unsubscribe();
        if (this.createDirectorySubscription != null) this.createDirectorySubscription.unsubscribe();
    }

    onCancelAction() {
        this.modal.hide()
    }

    onDirectoryChooseAction() {
        this.directoryChooserDialogService.onDirectoryChooseAction(this.selectedPathAsString);
        this.modal.hide()
    }

    createDirectory(): void {
        if (this.createDirectorySubscription != null) this.createDirectorySubscription.unsubscribe();
        this.createDirectorySubscription = this.fileTreeComponent.createDirectory().subscribe( (createdDirectory: FileTreeContainer) => {
            this.fileTreeComponent.setSelectedDirectory(createdDirectory);
        });
    }

    onSelectedPathChanges($event: Event) {
        this.checkIfSelectedPathIsValid();
    }

    private checkIfSelectedPathIsValid() {
        this.fileSystemService.getPathInfo(this.selectedPathAsString).subscribe((pathInfo: PathInfo) => {
            if (this.isTesterumProjectChooser) {
                this.isValidSelectedPath = pathInfo.isProjectDirectory;
                return;
            }

            if (!pathInfo.isValidPath) {
                this.isValidSelectedPath = false;
                return
            }

            if(this.showFiles) {
                this.isValidSelectedPath = pathInfo.isExistingPath;
            } else {
                this.isValidSelectedPath = (pathInfo.isExistingPath && pathInfo.canCreateChild) || !pathInfo.isExistingPath;
            }
        })
    }

    showCreateDirectoryButton(): boolean {
        return this.isValidSelectedPath && this.fileTreeComponent.getSelectedDir() != null;
    }
}
