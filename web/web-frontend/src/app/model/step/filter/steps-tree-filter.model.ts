import {JsonUtil} from "../../../utils/json.util";
import {StringUtils} from "../../../utils/string-utils.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class StepsTreeFilter implements Serializable<StepsTreeFilter>{
    showComposedTests: boolean = true;
    showBasicTest: boolean = true;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        return new StepsTreeFilter();
    }

    isEmpty(): boolean {
        if (this.showComposedTests != this.showBasicTest) {
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

    deserialize(input: Object): StepsTreeFilter {
        this.showComposedTests = input["showComposedTests"];
        this.showBasicTest = input['showBasicTest'];
        this.search = input['search'];
        for (let tag of (input['tags'] || [])) {
            this.tags.push(tag);
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"showComposedTests":' + JsonUtil.stringify(this.showComposedTests) +
            ',"showBasicTest":' + JsonUtil.stringify(this.showBasicTest) +
            ',"search":' + JsonUtil.stringify(this.search) +
            ',"tags":'+JsonUtil.serializeArray(this.tags)+
            '}'
    }
}
