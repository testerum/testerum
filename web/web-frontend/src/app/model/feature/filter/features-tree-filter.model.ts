import {Feature} from "../feature.model";
import {Attachment} from "../../file/attachment.model";
import {Path} from "../../infrastructure/path/path.model";
import {JsonUtil} from "../../../utils/json.util";

export class FeaturesTreeFilter implements Serializable<FeaturesTreeFilter>{
    showAutomatedTests: boolean = true;
    showManualTest: boolean = true;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        return new FeaturesTreeFilter();
    }

    deserialize(input: Object): FeaturesTreeFilter {
        this.showAutomatedTests = input["showAutomatedTests"];
        this.showManualTest = input['showManualTest'];
        this.search = input['search'];
        for (let tag of (input['tags'] || [])) {
            this.tags.push(tag);
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"showAutomatedTests":' + JsonUtil.stringify(this.showAutomatedTests) +
            ',"showManualTest":' + JsonUtil.stringify(this.showManualTest) +
            ',"search":' + JsonUtil.stringify(this.search) +
            ',"tags":'+JsonUtil.serializeArray(this.tags)+
            '}'
    }


}
