import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {FileDirChooserModalService} from "./file-dir-chooser-modal.service";
import {FileDirTreeComponent} from "../file-dir-tree/file-dir-tree.component";
import {JsonTreeService} from "../../../json-tree/json-tree.service";
import {JsonTreeNodeEventModel} from "../../../json-tree/event/selected-json-tree-node-event.model";
import {Subscription} from "rxjs";
import {FileSystemService} from "../../../../../service/file-system.service";
import {PathInfo} from "../../../../../model/infrastructure/path/path-info.model";
import {FileDirTreeContainerModel} from "../file-dir-tree/model/file-dir-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'file-dir-chooser-modal',
    templateUrl: 'file-dir-chooser-modal.component.html',
    styleUrls: ['file-dir-chooser-modal.component.scss']
})
export class FileDirChooserModalComponent implements OnInit, AfterViewInit, OnDestroy {

    @Input() showFiles: boolean;

    @ViewChild(FileDirTreeComponent) fileDirTreeComponent: FileDirTreeComponent;

    @ViewChild("infoModal") modal: ModalDirective;
    directoryChooserDialogService: FileDirChooserModalService;

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
            this.selectedPathAsString = (selectedNodeEvent.treeNode as FileDirTreeContainerModel).absoluteJavaPath;
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
        this.createDirectorySubscription = this.fileDirTreeComponent.createDirectory().subscribe( (createdDirectory: FileDirTreeContainerModel) => {
            this.fileDirTreeComponent.setSelectedDirectory(createdDirectory);
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

            this.isValidSelectedPath = (pathInfo.isExistingPath && pathInfo.canCreateChild) || !pathInfo.isExistingPath;
        })
    }
}
