import {StringUtils} from "./string-utils.util";
import {ObjectUtil} from "./object.util";

export class NameUtil {

    static getUniqueNameWithIndexSuffix(allKnownNames: string[], nameToAdd: string): string {
        if (this.isNameUnique(allKnownNames, nameToAdd)) {
            return nameToAdd;
        }
        let nameWithIndex = new NameWithIndex(nameToAdd);
        nameWithIndex.incrementIndex();

        while (!this.isNameUnique(allKnownNames, nameWithIndex.toString())) {
            nameWithIndex.incrementIndex()
        }
        return nameWithIndex.toString();
    }

    private static isNameUnique(allKnownNames: string[], nameToAdd: string): boolean {
        for (const knownName of allKnownNames) {
            if (StringUtils.equalsIgnoreCase(knownName, nameToAdd)) {
                return false;
            }
        }
        return true;
    }
}

class NameWithIndex {
    nameWithoutIndex: string;
    index: number;

    constructor(nameWithIndex: string) {
        let indexAsString = StringUtils.substringAfterLast(nameWithIndex, " ");
        if (indexAsString !== null && ObjectUtil.isANumber(indexAsString)) {
            this.nameWithoutIndex = StringUtils.substringBeforeLast(nameWithIndex, " ");
            this.index = ObjectUtil.getAsNumber(indexAsString);
        } else {
            this.nameWithoutIndex = nameWithIndex;
        }
    }

    incrementIndex() {
        if (this.index != null) {
            this.index = this.index + 1;
        } else {
            this.index = 1;
        }
    }

    toString(): string {
        return this.nameWithoutIndex + " " + this.index;
    }
}
