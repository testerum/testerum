import {AfterViewInit, ChangeDetectorRef, Component, ComponentRef, ViewChild} from '@angular/core';
import {SettingsService} from "../../../service/settings.service";
import {Setting} from "./model/setting.model";
import {ArrayUtil} from "../../../utils/array.util";
import {SettingType} from "./model/setting.type.enum";
import {ModalDirective} from "ngx-bootstrap";
import {InputTypeEnum} from "../../../generic/components/form/dynamic-input/model/input-type.enum";

@Component({
    selector: 'settings',
    templateUrl: 'settings-modal.component.html',
    styleUrls: ['settings-modal.component.scss'],
})
export class SettingsModalComponent implements AfterViewInit {

    @ViewChild("settingsModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<SettingsModalComponent>;

    selectedCategory: string;

    settings: Array<Setting> = [];
    settingsCategories: Array<string> = [];
    settingsByCategory: Map<string, Array<Setting>> = new Map<string, Array<Setting>>();

    constructor(private cd: ChangeDetectorRef,
                private settingsService: SettingsService) {}

    public init(settings: Array<Setting>) {
        this.updateCurrentSettings(settings);
        this.setSettingsByCategory(settings);
        if (this.settingsCategories.length > 0) {
            this.selectedCategory = this.settingsCategories[0];
        }
    }

    ngAfterViewInit(): void {
        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onCategorySelected(selectedCategory) {
        this.selectedCategory = selectedCategory;
        this.refresh();
    }

    getDynamicInputType(settingType: SettingType): InputTypeEnum {
        switch (settingType) {
            case SettingType.TEXT: return InputTypeEnum.TEXT;
            case SettingType.NUMBER: return InputTypeEnum.POSITIVE_INTEGER;
            case SettingType.FILESYSTEM_DIRECTORY: return InputTypeEnum.FILESYSTEM_DIRECTORY;
        }
    }

    cancel() {
        this.modal.hide();
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

    saveAction(): void {
        this.settingsService.save(this.settings).subscribe(
            (settings: Array<Setting>) => {
                this.init(settings);
                this.cancel();
            }
        );
    }

    resetToDefault(): void {
        this.settings.forEach((it: Setting) => {
            it.unresolvedValue = "";
            it.resolvedValue = "";
        });

        this.saveAction();
    }
}
