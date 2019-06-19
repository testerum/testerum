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
    selectedBrowser: string;

    driverSelectItems: SelectItem[] = [];
    selectedDriver: string;

    customInstallation: boolean = false;

    constructor(private seleniumDriversService: SeleniumDriversService) {
    }

    ngOnInit() {
        this.deserializedDefaultValue = this.defaultValue ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.defaultValue)) : new SeleniumDriverSettingValue();
        this.deserializedValue = this.value ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.value)) : new SeleniumDriverSettingValue();

        this.initInstallationSettings();

        let selectedBrowserAsSerialized: string = this.deserializedValue.browserType ? this.deserializedValue.browserType.asSerialized : this.deserializedDefaultValue.browserType.asSerialized;
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
                    this.selectedBrowser = stringSelectItem.value;
                    this.initDriverSelectedItems(key);
                }
            });
            this.browserSelectItems = browsersSelectedItems;
        });
    }

    private initDriverSelectedItems(selectedSeleniumBrowserType: SeleniumBrowserType) {
        let seleniumDriverInfos: SeleniumDriverInfo[] = this.driversInfoMap.get(selectedSeleniumBrowserType);

        let selectedDriverVersion = this.deserializedValue.driverVersion ? this.deserializedValue.driverVersion : this.deserializedDefaultValue.driverVersion;

        let driverSelectItems: SelectItem[] = [];
        for (const seleniumDriverInfo of seleniumDriverInfos) {
            let itemLabel = this.getSeleniumDriverLabel(seleniumDriverInfo);


            let driverSelectItem = new StringSelectItem(
                itemLabel,
                seleniumDriverInfo.driverVersion
            );

            driverSelectItems.push(driverSelectItem);

            if (selectedDriverVersion == null || selectedDriverVersion == seleniumDriverInfo.driverVersion) {
                selectedDriverVersion = seleniumDriverInfo.driverVersion;
                this.deserializedValue.driverVersion = seleniumDriverInfo.driverVersion;
                this.selectedDriver = seleniumDriverInfo.driverVersion;
            }
        }
        this.driverSelectItems = driverSelectItems;
    }

    private getSeleniumDriverLabel(seleniumDriverInfo: SeleniumDriverInfo): string {
        let result = "[";
        for (let i = 0; i < seleniumDriverInfo.browserVersions.length; i++) {
            if(i > 0) {result += " ,"}
            const browserVersion = seleniumDriverInfo.browserVersions[i];
            result +=browserVersion;
        }
        result += "] - Selenium driver: " + seleniumDriverInfo.driverVersion;

        return result;
    }

    private initInstallationSettings() {
        let installationPath: string = this.deserializedValue.browserExecutablePath ? this.deserializedValue.browserExecutablePath : null;
        this.customInstallation = installationPath != null;
    }

    onSelectedBrowserChanged(seleniumBrowser: SelectItem) {
        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(seleniumBrowser.value);
        this.deserializedValue.browserType = seleniumBrowserType;

        this.deserializedValue.driverVersion = null;
        this.selectedDriver = null;
        this.deserializedValue.browserExecutablePath = null;
        this.customInstallation = false;

        this.initDriverSelectedItems(seleniumBrowserType);
        this.triggerValueChanged();
    }

    onSelectedBrowserVersionChanged(selectedBrowserVersion: SelectItem) {
        this.deserializedValue.driverVersion = this.selectedDriver;
        this.triggerValueChanged();
    }

    supportsHeadlessSetting(): boolean {
        if(!this.selectedBrowser) return false;

        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(this.selectedBrowser);
        if (seleniumBrowserType == SeleniumBrowserType.CHROME || seleniumBrowserType == SeleniumBrowserType.FIREFOX) {
            return true;
        }
        return false;
    }

    onCustomInstallationSwitchChanged() {
        if (!this.customInstallation) {
            this.deserializedValue.browserExecutablePath = null;
            this.triggerValueChanged();
        }
    }

    triggerValueChanged() {
        let serializedValue = this.deserializedValue.serialize();
        this.valueChange.emit(serializedValue);
    }
}
