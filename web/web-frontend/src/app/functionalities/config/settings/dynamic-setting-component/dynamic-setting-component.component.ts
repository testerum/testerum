import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Setting} from "../model/setting.model";
import {SettingType} from "../model/setting.type.enum";
import {InputTypeEnum} from "../../../../generic/components/form/dynamic-input/model/input-type.enum";
import {SettingsUtil} from "../util/settings.util";
import {ArrayUtil} from "../../../../utils/array.util";
import {StringUtils} from "../../../../utils/string-utils.util";

@Component({
    selector: 'dynamic-setting-component',
    templateUrl: './dynamic-setting-component.component.html',
    styleUrls: ['./dynamic-setting-component.component.scss'],
    animations: [
        trigger('expandCollapse', [
            state('open', style({
                'height': '*'
            })),
            state('close', style({
                'height': '0px'
            })),
            transition('open => close', animate('400ms ease-in-out')),
            transition('close => open', animate('400ms ease-in-out'))
        ])
    ]
})
export class DynamicSettingComponentComponent implements OnInit {

    @Input() setting: Setting;
    @Output() valueChange = new EventEmitter();

    isCollapsible: boolean = false;

    collapsed: boolean = false;
    animationState: string = 'open';

    ngOnInit(): void {
        let uiHints = this.setting.definition.uiHints;
        if(ArrayUtil.containsElement(uiHints, "collapsible")) {
            this.isCollapsible = true

            if(ArrayUtil.containsElement(uiHints, "collapsedWhenEmpty") && StringUtils.isEmpty(this.setting.unresolvedValue)) {
                this.collapse();
            }
        }
    }

    getDynamicInputType(settingType: SettingType): InputTypeEnum {
        return SettingsUtil.getDynamicInputType(settingType);
    }

    //####### COLLAPSABLE PROP ######
    @Input('collapsed')
    set setCollapsed(value: boolean) {
        if (this.collapsed != value) {
            this.collapsed = value;
            this.animationState = this.collapsed ? 'close' : 'open';
        }
    }

    animate() {
        if (!this.isCollapsible) return;

        if (this.animationState == "close") {
            this.animationState = "open";
        } else {
            this.animationState = "close";
            this.collapsed = true; //this is only here and not on open to control the CSS "overflow: hidden;" required to autocomplete inputs
        }
    }

    collapse() {
        this.collapsed = true;
        this.animationState = "close";
    }

    expand() {
        this.collapsed = false;
        this.animationState = "open";
    }

    onAnimateEnd() {
        if (this.animationState == "close") {
            this.collapsed = true;
        } else {
            this.collapsed = false;
        }
    }

    onValueChange(newSettingValue: string) {
        this.valueChange.emit(newSettingValue)
        this.setting.unresolvedValue = newSettingValue
    }
}
