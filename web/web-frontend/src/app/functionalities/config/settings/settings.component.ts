import {Component, OnInit} from '@angular/core';
import {SettingsService} from "../../../service/settings.service";
import {Setting} from "./model/setting.model";
import {ArrayUtil} from "../../../utils/array.util";
import {SettingType} from "./model/setting.type.enum";


@Component({
    selector: 'settings',
    templateUrl: 'settings.component.html',
    styleUrls: ['settings.component.scss'],
})

export class SettingsComponent implements OnInit {

    PACKAGE_DIR_SETTING = "testerum.packageDirectory";
    BUILT_IN_BASIC_STEPS_DIRECTORY_SETTING = "testerum.builtInBasicStepsDirectory";

    settings: Array<Setting> = [];
    settingsCategories: Array<string> = [];
    settingsByCategory: Map<string, Array<Setting>> = new Map<string, Array<Setting>>();
    isEditMode = false;

    SettingTypeEnum = SettingType;

    constructor(private settingsService: SettingsService) {}

    ngOnInit() {
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                this.updateCurrentSettings(settings);
                this.setSettingsByCategory(settings);
            }
        );
    }

    private setSettingsByCategory(settings: Array<Setting>) {
        this.settingsCategories.length = 0;
        this.settingsByCategory.forEach((value, key) => {value.length = 0} );

        for (let setting of settings) {
            let settingCategory = setting.definition.category ? setting.definition.category : "Unknown";
            let settingMapValue = this.settingsByCategory.get(settingCategory);
            if (!settingMapValue) {
                settingMapValue = [];
            }
            settingMapValue.push(setting);
            settingMapValue.sort((left, right) => {
                if (left.definition.key == this.PACKAGE_DIR_SETTING) return -1;
                if (right.definition.key == this.PACKAGE_DIR_SETTING) return 1;
                if (left.definition.key == this.BUILT_IN_BASIC_STEPS_DIRECTORY_SETTING) return -1;
                if (right.definition.key == this.BUILT_IN_BASIC_STEPS_DIRECTORY_SETTING) return 1;

                return left.definition.key > right.definition.key ? 1 : -1
            });

            this.settingsByCategory.set(settingCategory, settingMapValue);

            if (!ArrayUtil.containsElement(this.settingsCategories, settingCategory)) {
                this.settingsCategories.push(settingCategory)
            }
        }

        ArrayUtil.sort(this.settingsCategories);
        if (ArrayUtil.containsElement(this.settingsCategories, "Unknown")) {
            ArrayUtil.removeElementFromArray(this.settingsCategories, "Unknown");
            this.settingsCategories.push("Unknown")
        }

        if (ArrayUtil.containsElement(this.settingsCategories, "Application")) {
            ArrayUtil.removeElementFromArray(this.settingsCategories, "Application");
            this.settingsCategories.splice(0, 0, "Application");
        }
    }

    private updateCurrentSettings(settings: Array<Setting>) {
        this.settings.length = 0;
        for (let setting of settings) {
            this.settings.push(setting)
        }
    }

    private findSettingByKey(settingKey: string): Setting {
        for (let setting of this.settings) {
            if(setting.definition.key == settingKey) {
                return setting
            }
        }
        return null;
    }

    isReadOnlyProperty(settingKey: string): boolean {
        if(settingKey == this.PACKAGE_DIR_SETTING) return true;
        if(settingKey == this.BUILT_IN_BASIC_STEPS_DIRECTORY_SETTING) return true;

        return false;
    }

    enableEditTestMode(): void {
        this.isEditMode = true;
    }

    cancelAction(): void {
        this.settingsService.getSettings().subscribe(
            (settings: Array<Setting>) => {
                this.updateCurrentSettings(settings);
                this.setSettingsByCategory(settings);
                this.isEditMode = false;
            }
        )
    }

    saveAction(): void {
        this.settingsService.save(this.settings).subscribe(
            (settings: Array<Setting>) => {
                this.updateCurrentSettings(settings);
                this.setSettingsByCategory(settings);
                this.isEditMode = false;
            }
        );
    }

    resetToDefault(): void {
        this.settings.forEach((it: Setting) => {
            it.unresolvedValue = null;
            it.resolvedValue = null;
        });

        this.saveAction();
    }
}