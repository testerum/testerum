import {ComponentRef, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {FileDirChooserModalComponent} from "./file-dir-chooser-modal.component";
import {AppComponent} from "../../../../../app.component";

@Injectable()
export class FileDirChooserModalService {

    private modalComponentRef: ComponentRef<FileDirChooserModalComponent>;
    private modalComponent: FileDirChooserModalComponent;
    private modalSubject: Subject<string>;

    showDirectoryChooserDialogModal(): Observable<string> {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(FileDirChooserModalComponent);
        let modalComponentRef = AppComponent.rootViewContainerRef.createComponent(factory);
        modalComponentRef.instance.directoryChooserDialogService = this;

        this.modalComponentRef = modalComponentRef;
        this.modalComponent = modalComponentRef.instance;
        this.modalSubject = new Subject<string>();

        return this.modalSubject.asObservable();
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
