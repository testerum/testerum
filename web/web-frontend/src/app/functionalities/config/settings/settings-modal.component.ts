import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {SettingsService} from "../../../service/settings.service";
import {Setting} from "./model/setting.model";
import {SettingType} from "./model/setting.type.enum";
import {ModalDirective} from "ngx-bootstrap";
import {InputTypeEnum} from "../../../generic/components/form/dynamic-input/model/input-type.enum";
import {SettingsUtil} from "./util/settings.util";

@Component({
    selector: 'settings',
    templateUrl: 'settings-modal.component.html',
    styleUrls: ['settings-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
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
        SettingsUtil.populateSettingsCategoriesNames(settings, this.settingsCategories);
        SettingsUtil.populateSettingsByCategoryMap(settings, this.settingsByCategory);
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
        return SettingsUtil.getDynamicInputType(settingType);
    }

    cancel() {
        this.modal.hide();
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
            it.unresolvedValue = it.definition.defaultValue;
            it.resolvedValue = it.definition.defaultValue;
        });
        this.refresh();
    }

    isComposedSetting(settingType: SettingType): boolean {
        return settingType == SettingType.SELENIUM_DRIVER;
    }
}
