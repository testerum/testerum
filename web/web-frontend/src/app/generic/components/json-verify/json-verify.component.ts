import {Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation} from '@angular/core';
import {JsonVerify} from "./model/json-verify.model";
import {JsonUtil} from '../../../utils/json.util';
import {StringUtils} from '../../../utils/string-utils.util';
import {JsonCompareModeEnum} from "./model/json-compare-mode.enum";
import {JsonVerifyEditorComponent} from "./json-verify-editor/json-verify-editor.component";

@Component({
    moduleId: module.id,
    selector: 'json-verify',
    templateUrl: 'json-verify.component.html',
    styleUrls: [
        'json-verify.component.scss'
    ],
    encapsulation: ViewEncapsulation.None
})
export class JsonVerifyComponent {

    @Input() model: string;
    @Output() modelChange = new EventEmitter<string>();
    @Input() editMode: boolean = true;

    isValidJson: boolean = true;
    selectedJsonCompareMode: JsonCompareModeEnum;

    @ViewChild(JsonVerifyEditorComponent) jsonVerifyEditorComponent: JsonVerifyEditorComponent;

    onTextChange(jsonAsString: string) {
        this.isValidJson = JsonUtil.isJson(jsonAsString);
        this.modelChange.emit(jsonAsString)
    }

    getJsonCompareModes(): Array<JsonCompareModeEnum> {
        return JsonCompareModeEnum.enums
    }

    onCompareModeChange(selectedJsonCompareMode: JsonCompareModeEnum) {
        this.selectedJsonCompareMode = selectedJsonCompareMode;
    }

    onSetCompareModeEvent() {
        if (this.selectedJsonCompareMode != null) {
            this.jsonVerifyEditorComponent.setCompareModeEvent(this.selectedJsonCompareMode)
        }
    }

    canAddCompareMode(): boolean {
        return this.isValidJson && this.selectedJsonCompareMode != null && this.editMode;
    }

    getCompareModeTooltip(): string {
        if (!this.isValidJson) {
            return "To insert a Compare Mode you need to have a valid JSON"
        }

        if (!this.canAddCompareMode()) {
            return "In order to insert a Compare Mode, please select one, set the cursor inside a JSON element and press Insert button"
        }

        return "Insert compare mode";
    }
}
