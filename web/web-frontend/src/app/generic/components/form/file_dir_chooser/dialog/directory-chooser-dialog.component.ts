import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {DirectoryChooserDialogService} from "./directory-chooser-dialog.service";
import {FileDirTreeComponent} from "../file-dir-tree/file-dir-tree.component";
import {FileDirTreeContainerModel} from "../file-dir-tree/model/file-dir-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'directory-chooser-dialog',
    templateUrl: 'directory-chooser-dialog.component.html',
    styleUrls: ['directory-chooser-dialog.component.scss']
})
export class DirectoryChooserDialogComponent implements AfterViewInit {

    @ViewChild(FileDirTreeComponent) fileDirTreeComponent: FileDirTreeComponent;

    @ViewChild("infoModal") modal: ModalDirective;
    directoryChooserDialogService: DirectoryChooserDialogService;

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.directoryChooserDialogService.clearModal()
        })
    }

    onCancelAction() {
        this.modal.hide()
    }

    onDirectoryChooseAction() {
        this.directoryChooserDialogService.onDirectoryChooseAction(this.fileDirTreeComponent.getSelectedPathAsString());
        this.modal.hide()
    }

    getSelectedNode(): FileDirTreeContainerModel {
        return this.fileDirTreeComponent.getSelectedNode();
    }

    getSelectedPathAsString(): string {
        return this.fileDirTreeComponent.getSelectedPathAsString();
    }
}
