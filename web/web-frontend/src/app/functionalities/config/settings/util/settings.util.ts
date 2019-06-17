import {Setting} from "../model/setting.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {SettingType} from "../model/setting.type.enum";
import {InputTypeEnum} from "../../../../generic/components/form/dynamic-input/model/input-type.enum";

export class SettingsUtil {

    static populateSettingsCategoriesNames(settings: Array<Setting>, settingsCategories: Array<string>): Array<string> {
        settingsCategories.length = 0;

        for (let setting of settings) {
            let settingCategory = setting.definition.category ? setting.definition.category : "Unknown";
            if (!ArrayUtil.containsElement(settingsCategories, settingCategory)) {
                settingsCategories.push(settingCategory);
            }
        }

        ArrayUtil.sort(settingsCategories);
        if (ArrayUtil.containsElement(settingsCategories, "Unknown")) {
            ArrayUtil.removeElementFromArray(settingsCategories, "Unknown");
            settingsCategories.push("Unknown")
        }

        return settingsCategories;
    }

    static populateSettingsByCategoryMap(settings: Array<Setting>, settingsByCategory: Map<string, Array<Setting>>): Map<string, Array<Setting>> {
        settingsByCategory.forEach((value, key) => {value.length = 0} );

        for (let setting of settings) {
            let settingCategory = setting.definition.category ? setting.definition.category : "Unknown";
            let settingMapValue = settingsByCategory.get(settingCategory);
            if (!settingMapValue) {
                settingMapValue = [];
            }
            settingMapValue.push(setting);

            settingsByCategory.set(settingCategory, settingMapValue);
        }

        return settingsByCategory;
    }

    static getDynamicInputType(settingType: SettingType): InputTypeEnum {
        switch (settingType) {
            case SettingType.BOOLEAN: return InputTypeEnum.BOOLEAN;
            case SettingType.TEXT: return InputTypeEnum.TEXT;
            case SettingType.NUMBER: return InputTypeEnum.POSITIVE_INTEGER;
            case SettingType.ENUM: return InputTypeEnum.ENUM;
            case SettingType.FILESYSTEM_DIRECTORY: return InputTypeEnum.FILESYSTEM_DIRECTORY;
            case SettingType.SELENIUM_DRIVER: return InputTypeEnum.SELENIUM_DRIVER;
        }
    }
}
