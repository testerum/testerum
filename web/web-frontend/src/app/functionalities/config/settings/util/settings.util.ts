import {Setting} from "../model/setting.model";
import {ArrayUtil} from "../../../../utils/array.util";

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
            settingMapValue.sort((left, right) => {
                return left.definition.key > right.definition.key ? 1 : -1
            });

            settingsByCategory.set(settingCategory, settingMapValue);
        }

        return settingsByCategory;
    }
}
