import {Component, EventEmitter, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {JsonTreeContainerEditorEvent} from "./model/json-tree-container-editor.event";
import {JsonTreeContainerEditorEnum} from "./model/enums/json-tree-container-editor.enum";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {BsModalRef, ModalDirective} from "ngx-bootstrap";
import {JsonTreeContainer} from "../model/json-tree-container.model";
import {ValidationResultModel} from "../../../../model/infrastructure/validation-result.model";
import {StringUtils} from "../../../../utils/string-utils.util";
import {ArrayUtil} from "../../../../utils/array.util";
import {NgForm} from "@angular/forms";
import {FormUtil} from "../../../../utils/form.util";
import {isValid} from "ngx-bootstrap/chronos/create/valid";

@Component({
    moduleId: module.id,
    selector: 'json-tree-container-editor',
    templateUrl: 'json-tree-container-editor.component.html',
    styleUrls:['json-tree-container-editor.component.css', '../../../../generic/css/generic.css', '../../../../generic/css/forms.css']
})
export class JsonTreeContainerEditor {
    @Input() title: string;
    @Input() name: string;
    private actionType: JsonTreeContainerEditorEnum;
    oldName: string;
    isFile: boolean;

    @ViewChild(NgForm) form: NgForm;

    private siblingsNodeNames: Array<string> = null;

    editorEventEmitter: EventEmitter<JsonTreeContainerEditorEvent> = null;

    JsonTreeContainerEditorEnum = JsonTreeContainerEditorEnum;

    constructor(public bsModalRef: BsModalRef) {}

    showToUpdateContainerName(name: string, siblingsNodeNames: Array<string>): EventEmitter<JsonTreeContainerEditorEvent> {
        this.siblingsNodeNames = siblingsNodeNames;

        this.actionType = JsonTreeContainerEditorEnum.UPDATE;
        this.name = name;
        this.oldName = name;
        this.editorEventEmitter = new EventEmitter<JsonTreeContainerEditorEvent>();
        return this.editorEventEmitter;
    }

    showToCreateContainer(siblingsNodeNames: Array<string>): EventEmitter<JsonTreeContainerEditorEvent> {
        this.siblingsNodeNames = siblingsNodeNames;

        this.actionType = JsonTreeContainerEditorEnum.CREATE;
        this.name = null;
        this.oldName = null;
        this.editorEventEmitter = new EventEmitter<JsonTreeContainerEditorEvent>();
        return this.editorEventEmitter;
    }

    showToDeleteContainer(name: string): EventEmitter<JsonTreeContainerEditorEvent> {
        this.actionType = JsonTreeContainerEditorEnum.DELETE;
        this.name = name;
        this.oldName = name;
        this.editorEventEmitter = new EventEmitter<JsonTreeContainerEditorEvent>();
        return this.editorEventEmitter;
    }

    showToCopyContainer(pathToCopy: Path, destinationPath: Path): EventEmitter<JsonTreeContainerEditorEvent> {
        this.actionType = JsonTreeContainerEditorEnum.COPY;
        this.name = destinationPath.toString();
        this.oldName = pathToCopy.toString();
        this.isFile = pathToCopy.isFile();
        this.editorEventEmitter = new EventEmitter<JsonTreeContainerEditorEvent>();
        return this.editorEventEmitter;
    }

    hide(): void {
        this.editorEventEmitter.complete();
        this.bsModalRef.hide();
    }

    ok(): void {
        if (!this.isValid()) {
            return
        }

        let result = new JsonTreeContainerEditorEvent();
        result.eventType = this.actionType;
        result.oldName = this.oldName;
        result.newName = this.name;

        this.editorEventEmitter.emit(result);
        this.editorEventEmitter.complete();

        this.hide();
    }

    isValid(): boolean {
        if (this.actionType == JsonTreeContainerEditorEnum.CREATE) {
            if (ArrayUtil.containsElement(this.siblingsNodeNames, this.name)) {
                FormUtil.addErrorToForm(this.form, "packageName", "directory_already_exists");
                return false
            }
        }
        if (this.actionType == JsonTreeContainerEditorEnum.UPDATE) {
            if (ArrayUtil.containsElement(this.siblingsNodeNames, this.name)) {
                FormUtil.addErrorToForm(this.form, "packageName", "directory_already_exists");
                return false
            }
        }

        return true
    }
}
