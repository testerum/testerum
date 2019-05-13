import {ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {RunnerConfigService} from "../runner-config.service";
import {Subscription} from "rxjs";
import {RunnerConfig} from "../model/runner-config.model";
import {FormUtil} from "../../../../utils/form.util";
import {NgForm} from "@angular/forms";
import {SettingsUtil} from "../../settings/util/settings.util";
import {Setting} from "../../settings/model/setting.model";
import {SettingType} from "../../settings/model/setting.type.enum";
import {InputTypeEnum} from "../../../../generic/components/form/dynamic-input/model/input-type.enum";

@Component({
    selector: 'runner-config-editor',
    templateUrl: './runner-config-editor.component.html',
    styleUrls: ['./runner-config-editor.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class RunnerConfigEditorComponent implements OnInit, OnDestroy {

    @ViewChild(NgForm) form: NgForm;

    runnerConfig: RunnerConfig|null;

    activeTabIndex: number = 0;
    settingsCategories: Array<string> = [];
    settingsByCategory: Map<string, Array<Setting>> = new Map<string, Array<Setting>>();

    private selectedRunnerSubscription: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private runnerConfigService: RunnerConfigService) {
    }

    ngOnInit() {
        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfigs: Array<RunnerConfig>) =>{
            if(runnerConfigs.length == 0 || runnerConfigs.length > 1) {
                this.runnerConfig = null;
            } else {
                this.runnerConfig = runnerConfigs[0];
                this.activeTabIndex = 0;
                SettingsUtil.populateSettingsCategoriesNames(this.runnerConfigService.settings, this.settingsCategories);
                SettingsUtil.populateSettingsByCategoryMap(this.runnerConfigService.settings, this.settingsByCategory)
            }
        });
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    hasRunnerConfigs(): boolean {
        return this.runnerConfigService.runners.length > 0
    }

    onNameChange(event: any): void {
        let runnerConfigByName = this.runnerConfigService.getRunnerConfigByName(this.runnerConfig.name);
        if (runnerConfigByName != this.runnerConfig) {
            FormUtil.addErrorToForm(this.form, "name", "a_resource_with_the_same_name_already_exist");
        }
        this.runnerConfigService.refreshConfigListEventEmitter.emit();
    }

    getDynamicInputType(settingType: SettingType): InputTypeEnum {
        return SettingsUtil.getDynamicInputType(settingType);
    }

    onSettingChange(settingValue: string, setting: Setting) {
        if (settingValue == null) {
            this.runnerConfig.settings.delete(setting.definition.key);
            return
        }
        console.log("settingValue", settingValue);

        this.runnerConfig.settings.set(setting.definition.key, settingValue);
    }

    getValueForSetting(setting: Setting): string {
        let settingValue = this.runnerConfig.settings.get(setting.definition.key);

        return settingValue
    }
}
