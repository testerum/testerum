import {Component, Input} from '@angular/core';
import {Setting} from "../../model/setting.model";
import {StringUtils} from "../../../../../utils/string-utils.util";

@Component({
    selector: 'selenium-advanced',
    templateUrl: './selenium-advanced.component.html',
    styleUrls: ['./selenium-advanced.component.scss']
})
export class SeleniumAdvancedComponent {

    @Input() setting: Setting;

    hasChanges(): boolean {
        return StringUtils.isNotEmpty(this.setting.unresolvedValue)
    }
}
