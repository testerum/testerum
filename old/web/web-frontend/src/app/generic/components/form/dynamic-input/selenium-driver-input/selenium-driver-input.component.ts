import {
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewEncapsulation
} from '@angular/core';
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
export class SeleniumDriverInputComponent implements OnInit, OnChanges {

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
    remoteUrl: string;

    customBrowserDriver: boolean = false;
    customInstallation: boolean = false;

    constructor(private cd: ChangeDetectorRef,
                private seleniumDriversService: SeleniumDriversService) {
    }

    ngOnInit() {
        this.deserializedDefaultValue = this.defaultValue ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.defaultValue)) : new SeleniumDriverSettingValue();
        if (this.value) {
            this.deserializedValue = new SeleniumDriverSettingValue().deserialize(JSON.parse(this.value))
        } else {
            this.deserializedValue = this.defaultValue ? new SeleniumDriverSettingValue().deserialize(JSON.parse(this.defaultValue)) : new SeleniumDriverSettingValue();
        }

        this.initBrowserDriver();
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

            this.refresh();
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['value']) {
            this.ngOnInit();
        }
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
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

    private initBrowserDriver() {
        let browserDriver: string = this.deserializedValue.driverVersion ? this.deserializedValue.driverVersion : null;
        this.customBrowserDriver = browserDriver != null;
    }

    private initInstallationSettings() {
        let installationPath: string = this.deserializedValue.browserExecutablePath ? this.deserializedValue.browserExecutablePath : null;
        this.customInstallation = installationPath != null;
    }

    onSelectedBrowserChanged(seleniumBrowser: SelectItem) {
        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(seleniumBrowser.value);
        this.deserializedValue.browserType = seleniumBrowserType;

        this.deserializedValue.driverVersion = null;
        this.deserializedValue.browserExecutablePath = null;
        this.deserializedValue.headless = this.deserializedDefaultValue.headless;
        this.deserializedValue.remoteUrl = null;
        this.customBrowserDriver = false;
        this.customInstallation = false;
        this.selectedDriver = null;
        this.remoteUrl = null;

        this.initDriverSelectedItems(seleniumBrowserType);
        this.triggerValueChanged();
    }

    onCustomBrowserDriverSwitchChanged() {
        if (!this.customBrowserDriver) {
            this.deserializedValue.driverVersion = null;
            this.triggerValueChanged();
        } else {
            this.selectedDriver = this.driverSelectItems ? this.driverSelectItems[0].value : null;
            this.deserializedValue.driverVersion = this.selectedDriver;
            this.triggerValueChanged();
        }
    }

    onSelectedBrowserVersionChanged(selectedBrowserVersion: SelectItem) {
        this.deserializedValue.driverVersion = this.selectedDriver;
        this.triggerValueChanged();
    }

    supportsCustomDriverVersion(): boolean {
        if(!this.selectedBrowser) return false;

        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(this.selectedBrowser);
        if (seleniumBrowserType == SeleniumBrowserType.CHROME ||
            seleniumBrowserType == SeleniumBrowserType.FIREFOX ||
            seleniumBrowserType == SeleniumBrowserType.OPERA ||
            seleniumBrowserType == SeleniumBrowserType.EDGE ||
            seleniumBrowserType == SeleniumBrowserType.INTERNET_EXPLORER ||
            seleniumBrowserType == SeleniumBrowserType.SAFARI) {
            return true;
        }
        return false;
    }

    supportsRemoteUrl(): boolean {
        if(!this.selectedBrowser) return false;

        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(this.selectedBrowser);
        if (seleniumBrowserType == SeleniumBrowserType.REMOTE) {
            return true;
        }
        return false;
    }

    supportsHeadlessSetting(): boolean {
        if(!this.selectedBrowser) return false;

        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(this.selectedBrowser);
        if (seleniumBrowserType == SeleniumBrowserType.CHROME || seleniumBrowserType == SeleniumBrowserType.FIREFOX) {
            return true;
        }
        return false;
    }

    supportsCustomInstallation(): boolean {
        if(!this.selectedBrowser) return false;

        let seleniumBrowserType = SeleniumBrowserType.fromSerialization(this.selectedBrowser);
        if (seleniumBrowserType == SeleniumBrowserType.CHROME ||
            seleniumBrowserType == SeleniumBrowserType.FIREFOX ||
            seleniumBrowserType == SeleniumBrowserType.OPERA) {
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
