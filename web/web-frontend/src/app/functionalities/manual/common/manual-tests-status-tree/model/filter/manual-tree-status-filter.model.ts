import {StringUtils} from "../../../../../../utils/string-utils.util";
import {JsonUtil} from "../../../../../../utils/json.util";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class ManualTreeStatusFilterModel implements Serializable<ManualTreeStatusFilterModel> {
    showNotExecuted = false;
    showPassed = false;
    showFailed = false;
    showBlocked = false;
    showNotApplicable = false;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        let manualTreeStatusFilterModel = new ManualTreeStatusFilterModel();
        manualTreeStatusFilterModel.showNotExecuted = true;
        manualTreeStatusFilterModel.showPassed = true;
        manualTreeStatusFilterModel.showFailed = true;
        manualTreeStatusFilterModel.showBlocked = true;
        manualTreeStatusFilterModel.showNotApplicable = true;
        return manualTreeStatusFilterModel;
    }

    isEmpty(): boolean {
        if (!this.showNotExecuted
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
            ',"showPassed":' + JsonUtil.stringify(this.showPassed) +
            ',"showFailed":' + JsonUtil.stringify(this.showFailed) +
            ',"showBlocked":' + JsonUtil.stringify(this.showBlocked) +
            ',"showNotApplicable":' + JsonUtil.stringify(this.showNotApplicable) +
            ',"search":' + JsonUtil.stringify(this.search) +
            ',"tags":'+JsonUtil.serializeArray(this.tags)+
            '}'
    }

    clone(): ManualTreeStatusFilterModel {
        let serializedData = this.serialize();
        return new ManualTreeStatusFilterModel().deserialize(JSON.parse(serializedData));
    }
}
