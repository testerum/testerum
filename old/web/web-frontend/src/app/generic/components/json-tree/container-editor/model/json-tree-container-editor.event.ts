
import {JsonTreeContainerEditorEnum} from "./enums/json-tree-container-editor.enum";

export class JsonTreeContainerEditorEvent {
    eventType: JsonTreeContainerEditorEnum;
    oldName: string;
    newName: string;
}
