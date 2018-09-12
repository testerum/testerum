import {StringUtils} from "../../../../../../utils/string-utils.util";
import {JsonUtil} from "../../../../../../utils/json.util";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class ManualTreeStatusFilterModel implements Serializable<ManualTreeStatusFilterModel> {
    showNotExecuted = true;
    showInProgress = true;
    showPassed = true;
    showFailed = true;
    showBlocked = true;
    showNotApplicable = true;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        return new ManualTreeStatusFilterModel();
    }

    isEmpty(): boolean {
        if (!this.showNotExecuted
            || !this.showInProgress
            || !this.showPassed
            || !this.showFailed
            || !this.showBlocked
            || !this.showNotApplicable) {
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

    deserialize(input: Object): ManualTreeStatusFilterModel {
        this.showNotExecuted = input["showNotExecuted"];
        this.showInProgress = input['showInProgress'];
        this.showPassed = input['showPassed'];
        this.showFailed = input['showFailed'];
        this.showBlocked = input['showBlocked'];
        this.showNotApplicable = input['showNotApplicable'];
        this.search = input['search'];
        for (let tag of (input['tags'] || [])) {
            this.tags.push(tag);
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"showNotExecuted":' + JsonUtil.stringify(this.showNotExecuted) +
            ',"showInProgress":' + JsonUtil.stringify(this.showInProgress) +
            ',"showPassed":' + JsonUtil.stringify(this.showPassed) +
            ',"showFailed":' + JsonUtil.stringify(this.showFailed) +
            ',"showBlocked":' + JsonUtil.stringify(this.showBlocked) +
            ',"showNotApplicable":' + JsonUtil.stringify(this.showNotApplicable) +
            ',"search":' + JsonUtil.stringify(this.search) +
            ',"tags":'+JsonUtil.serializeArray(this.tags)+
            '}'
    }
}
