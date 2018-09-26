import {JsonUtil} from "../../../utils/json.util";
import {StringUtils} from "../../../utils/string-utils.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class FeaturesTreeFilter implements Serializable<FeaturesTreeFilter>{
    includeAutomatedTests: boolean = true;
    includeManualTests: boolean = true;
    includeEmptyFeatures: boolean = true;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        return new FeaturesTreeFilter();
    }

    isEmpty(): boolean {
        if (this.includeAutomatedTests != this.includeManualTests) {
            return false;
        }

        if (!StringUtils.isEmpty(this.search)) {
            return false;
        }

        if (this.tags.length > 0) {
            return false;
        }

        return true
    }

    deserialize(input: Object): FeaturesTreeFilter {
        this.includeAutomatedTests = input["includeAutomatedTests"];
        this.includeManualTests = input['includeManualTests'];
        this.includeEmptyFeatures = input['includeEmptyFeatures'];
        this.search = input['search'];
        for (let tag of (input['tags'] || [])) {
            this.tags.push(tag);
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"includeAutomatedTests":' + JsonUtil.stringify(this.includeAutomatedTests) +
            ',"includeManualTests":' + JsonUtil.stringify(this.includeManualTests) +
            ',"includeEmptyFeatures":' + JsonUtil.stringify(this.includeEmptyFeatures) +
            ',"search":' + JsonUtil.stringify(this.search) +
            ',"tags":'+JsonUtil.serializeArray(this.tags)+
            '}'
    }
}
