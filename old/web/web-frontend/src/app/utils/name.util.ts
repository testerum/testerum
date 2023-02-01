import {StringUtils} from "./string-utils.util";
import {ObjectUtil} from "./object.util";

export class NameUtil {

    static readonly COPY_SUFFIX = " - Copy";

    static getUniqueNameWithCopyAndIndexSuffix(allKnownNames: string[], nameToAdd: string): string {
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
    rootName: string;
    index: number = 0;

    constructor(fullName: string) {
        let indexAsString = StringUtils.substringAfterLast(fullName, " ");
        if (indexAsString !== null && ObjectUtil.isANumber(indexAsString)) {
            let nameWithoutIndex = StringUtils.substringBeforeLast(fullName, " ");
            let nameWithoutCopySuffix = StringUtils.substringBeforeLast(nameWithoutIndex, NameUtil.COPY_SUFFIX);
            if (nameWithoutCopySuffix) {
                this.rootName = nameWithoutCopySuffix;
                this.index = ObjectUtil.getAsNumber(indexAsString);
            } else {
                this.rootName = fullName;
            }
        } else {
            this.rootName = fullName;
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
        return this.rootName + NameUtil.COPY_SUFFIX + " " + this.index;
    }
}
