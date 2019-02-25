import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {FileDirChooserModalService} from "./file-dir-chooser-modal.service";
import {FileDirTreeComponent} from "../file-dir-tree/file-dir-tree.component";
import {FileDirTreeContainerModel} from "../file-dir-tree/model/file-dir-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'file-dir-chooser-modal',
    templateUrl: 'file-dir-chooser-modal.component.html',
    styleUrls: ['file-dir-chooser-modal.component.scss']
})
export class FileDirChooserModalComponent implements AfterViewInit {

    @ViewChild(FileDirTreeComponent) fileDirTreeComponent: FileDirTreeComponent;

    @ViewChild("infoModal") modal: ModalDirective;
    directoryChooserDialogService: FileDirChooserModalService;

    isTesterumProjectChooser: boolean = false;

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

    createDirectory(): void {
        this.fileDirTreeComponent.createDirectory()
    }
}
