import {ComponentRef, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {FileChooserModalComponent} from "./file-chooser-modal.component";
import {AppComponent} from "../../../../../app.component";

@Injectable()
export class FileChooserModalService {

    private modalComponentRef: ComponentRef<FileChooserModalComponent>;
    private modalComponent: FileChooserModalComponent;
    private modalSubject: Subject<string>;

    showDirectoryChooserDialogModal(showFiles: boolean = false): Observable<string> {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(FileChooserModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.directoryChooserDialogService = this;

        modalComponentRef.instance.showFiles = showFiles;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
        this.modalSubject = new Subject<string>();

        return this.modalSubject.asObservable();
    }

    showTesterumProjectChooserModal(): Observable<string> {

        let observable = this.showDirectoryChooserDialogModal();
        this.modalComponent.isTesterumProjectChooser = true;

        return observable;
    }

    onDirectoryChooseAction(selectedPath: string) {
        this.modalSubject.next(selectedPath);
    }

    clearModal() {
        this.modalSubject.complete();
        this.modalComponentRef.destroy();

        this.modalComponentRef = null;
        this.modalComponent = null;
        this.modalSubject = null;
    }
}
