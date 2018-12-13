import {ComponentRef, Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {DirectoryChooserDialogComponent} from "./directory-chooser-dialog.component";
import {AppComponent} from "../../../../../app.component";

@Injectable()
export class DirectoryChooserDialogService {

    private modalComponentRef: ComponentRef<DirectoryChooserDialogComponent>;
    private modalComponent: DirectoryChooserDialogComponent;
    private modalSubject: Subject<string>;

    showDirectoryChooserDialogModal(): Observable<string> {

        const factory = AppComponent.componentFactoryResolver.resolveComponentFactory(DirectoryChooserDialogComponent);
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
