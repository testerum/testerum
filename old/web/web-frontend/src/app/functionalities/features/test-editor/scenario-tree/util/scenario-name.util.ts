import {StringUtils} from "../../../../../utils/string-utils.util";
import {ObjectUtil} from "../../../../../utils/object.util";

export class ScenarioNameUtil {

    static readonly NAME_PREFIX = "Scenario ";

    static getNextDefaultName(allKnownNames: string[]): string {
        let otherNamesMaxDefaultIndex = this.getOtherNamesMaxDefaultIndex(allKnownNames);
        let newNameIndex = otherNamesMaxDefaultIndex + 1;
        return this.NAME_PREFIX + newNameIndex;
    }

    private static getOtherNamesMaxDefaultIndex(allKnownNames: string[]): number {
        let maxIndex = 0;
        for (const knownName of allKnownNames) {
            if(knownName != null && knownName.startsWith(this.NAME_PREFIX)) {
                let defaultNameSuffix = StringUtils.substringAfter(knownName, this.NAME_PREFIX);
                if (defaultNameSuffix !== null && ObjectUtil.isANumber(defaultNameSuffix)) {
                    let index = ObjectUtil.getAsNumber(defaultNameSuffix);
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                }
            }
        }
        return maxIndex;
    }
}

