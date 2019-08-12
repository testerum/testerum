import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {JsonUtil} from '../../../utils/json.util';
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
export class JsonVerifyComponent implements OnChanges {

    @Input() model: string;
    @Output() modelChange = new EventEmitter<string>();
    @Input() editMode: boolean = true;

    isValidJson: boolean = true;
    selectedJsonCompareMode: JsonCompareModeEnum;

    @ViewChild(JsonVerifyEditorComponent) jsonVerifyEditorComponent: JsonVerifyEditorComponent;

    onTextChange(jsonAsString: string) {
        this.modelChange.emit(jsonAsString);
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.checkAndSetIfIsValidJson();
    }

    private checkAndSetIfIsValidJson() {
        this.isValidJson = JsonUtil.isJson(this.model);
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
        return this.selectedJsonCompareMode != null && this.editMode;
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

    getJsonVerifyExplanation(): string {
        return "JSON Verify allows you to check if your JSON is matching this expected JSON.\n" +
            "The simplest comparison is to paste the JSON you are expecting and remove the nodes that you don't care in your comparison.\n" +
            "\n" +
            "**Comparison Functions**\n" +
            "`" +
            "{\n" +
            "  \"id\": \"@isNotEmpty()\",\n" +
            "  \"firstName\": \"John\",\n" +
            "  \"lastName\": \"Doe\",\n" +
            "}\n" +
            "`\n" +
            "`@isNotEmpty()` function allows you to check that the \"id\" field has a value without carring exactly what this value is.\n" +
            "Other functions supported are:  `@isNotNull()`, `@isNullOrEmpty()`, `@isNotEmpty()`, `@isBlank()`, `@isNullOrBlank()`, `@isNotBlank()`, `@matchesRegex(\"[0-9]{2}\")`'.\n" +
            "#### **Comparison Modes**\n" +
            "For each node in the JSON you can setup a comparison mode. A comparison mode can be defined using the field `\"=compareMode\": \"exact\"`.\n" +
            "All the possible comparison modes are: `contains`, `exact` or `unorderedExact`. If you define a comparison mode on a node, it will be inherited on the children nodes until is explicitly overwritten.\n" +
            "#### **Comparison Modes - CONTAINS**\n" +
            "The default comparison mode is `contains`. This mode allows you to define only the nodes you want to compare.\n" +
            "For example:\n" +
            "**Actual JSON**\n" +
            "`{\n" +
            "    \"id\": 3476,\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Doe\"\n" +
            "}`\n" +
            "**Expected JSON**\n" +
            "`{\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Doe\"\n" +
            "}`\n" +
            "Because the default comparison mode is `contains`, the \"Actual JSON\" will match the \"Expected JSON\", even though the \"Actual JSON\" has extra fields.\n" +
            "#### **Comparison Modes - EXACT**\n" +
            "You can change the compare mode by adding the element `\"=compareMode\": \"exact\"` in the node you want.\n" +
            "**Actual JSON**\n" +
            "`{\n" +
            "    \"id\": 3476,\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Doe\"\n" +
            "}`\n" +
            "**Expected JSON**\n" +
            "`{\n" +
            "    \"=compareMode\": \"exact\",\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Doe\"\n" +
            "}`\n" +
            "This comparison will **fail** because the \"Actual JSON\" has the \"id\" field and the \"Expected JSON\" has the compare mode exact and is missing this field\n" +
            "#### **Comparison Modes - UNORDERED EXACT**\n" +
            "The compare mode `unorderedExact` is like `exact`, but ignores the order of items in an array:\n" +
            "**Actual JSON**\n" +
            "`[2, 1, 3]`\n" +
            "**Expected JSON**\n" +
            "`[\"=compareMode: unorderedExact\"1, 2, 3]`\n" +
            "This will match."

    }

    onFormatJsonEvent() {
        this.jsonVerifyEditorComponent.formatJson()
    }
}
