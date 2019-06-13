import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';
import {SeleniumDriversService} from "./selenium-drivers.service";
import {SeleniumBrowserType} from "./model/selenium-browser-type.enum";
import {SeleniumDriverInfo} from "./model/selenium-driver-info.model";
import {SelectItem} from "primeng/api";
import {StringSelectItem} from "../../../../../model/prime-ng/StringSelectItem";
import {SeleniumDriverSettingValue} from "./model/selenium-driver-setting-value.model";

@Component({
    selector: 'selenium-driver-input',
    templateUrl: './selenium-driver-input.component.html',
    styleUrls: ['./selenium-driver-input.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class SeleniumDriverInputComponent implements OnInit {

    @Input() defaultValue: string;
    @Input() value: string;
    @Output() valueChange = new EventEmitter();

    deserializedDefaultValue: SeleniumDriverSettingValue;
    deserializedValue: SeleniumDriverSettingValue;

    driversInfoMap: Map<SeleniumBrowserType, SeleniumDriverInfo[]> = new Map<SeleniumBrowserType, SeleniumDriverInfo[]>();
    browserSelectItems: SelectItem[] = [];
    selectedBrowser: SelectItem;

    constructor(private seleniumDriversService: SeleniumDriversService) {
    }

    ngOnInit() {
        this.deserializedDefaultValue = this.defaultValue ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.defaultValue)) : null;
        this.deserializedValue = this.value ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.value)) : null;

        let selectedBrowserAsSerialized: string = this.deserializedValue ? this.deserializedValue.browserType.asSerialized : this.deserializedDefaultValue.browserType.asSerialized;

        this.seleniumDriversService.getDriversInfo().subscribe((driversInfoMap: Map<SeleniumBrowserType, SeleniumDriverInfo[]>) => {
            this.driversInfoMap = driversInfoMap;

            let browsersSelectedItems = [];
            driversInfoMap.forEach((value: SeleniumDriverInfo[], key: SeleniumBrowserType) => {
                let stringSelectItem = new StringSelectItem(
                    key.toString(),
                    key.asSerialized,
                    false,
                    key.icon
                );

                browsersSelectedItems.push(
                    stringSelectItem
                );

                if (stringSelectItem.value == selectedBrowserAsSerialized) {
                    this.selectedBrowser = stringSelectItem;
                }
            });
            this.browserSelectItems = browsersSelectedItems;
        });
    }

    onSelectedBrowserChanged(seleniumBrowser: SelectItem) {
    }
}
