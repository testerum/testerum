import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {JsonVerify} from "./model/json-verify.model";
import {JsonUtil} from '../../../utils/json.util';

@Component({
    moduleId: module.id,
    selector: 'json-verify',
    templateUrl: 'json-verify.component.html',
    styleUrls: [
        'json-verify.component.scss'
    ],
    encapsulation: ViewEncapsulation.None
})
export class JsonVerifyComponent implements OnInit {

    @Input() model: JsonVerify;
    @Input() editMode: boolean = true;

    sampleJsonText: string;

    isTreeVisible: boolean = false;
    isValidJson: boolean = true;

    ngOnInit() {
        if (this.model == null) {
            this.model = new JsonVerify();
        }
    }

    onToggleEditorMode() {
        this.isTreeVisible = !this.isTreeVisible;
    }

    isEmptyModel(): boolean {
        return this.model.isEmpty();
    }

    shouldDisplayJsonSample(): boolean {
        return this.editMode && (this.isEmptyModel() || this.sampleJsonText != null);
    }

    onTextChange(jsonAsString: string) {
        this.isValidJson = JsonUtil.isJson(jsonAsString);
    }
}
