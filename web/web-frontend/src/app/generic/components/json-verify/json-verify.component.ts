import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';
import {JsonVerify} from "./model/json-verify.model";
import {JsonUtil} from '../../../utils/json.util';
import {StringUtils} from '../../../utils/string-utils.util';

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

    onTextChange(jsonAsString: string) {
        this.isValidJson = JsonUtil.isJson(jsonAsString);
        this.modelChange.emit(jsonAsString)
    }
}
