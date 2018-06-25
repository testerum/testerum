
import {ResourceEditorInterface} from "./resource-editor.interface";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ResourceContext} from "../../../model/resource/resource-context.model";
import {EventEmitter, Input, Output, ViewChild} from "@angular/core";
import {NgForm} from "@angular/forms";

//TODO: deprecated
export abstract class ResourceEditor<T> implements ResourceEditorInterface{

    resource: ResourceContext<T>;
    editMode: boolean = false;
    @Input() isModalMode: boolean = false;
    @ViewChild(NgForm) form: NgForm;

    @Output() createResourceEventEmitter: EventEmitter<Path> = new EventEmitter<Path>();

    abstract initInCreateMode(): void;

    abstract initFromResourcePath(resourcePath: Path): void;

    abstract saveAction(): void;

    abstract cancelAction(): void;

    abstract deleteAction(): void;

    isFormValid(): boolean {
        return this.form.valid;
    }

    isCreateNewResource(): boolean {
        return this.resource.oldPath == null;
    }

    setEditMode(isEditMode: boolean): void {
        this.editMode = isEditMode;
    }

    isEditMode(): boolean {
        return this.editMode;
    }
}
