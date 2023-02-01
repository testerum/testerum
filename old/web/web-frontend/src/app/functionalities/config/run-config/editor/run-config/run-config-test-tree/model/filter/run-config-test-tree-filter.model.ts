import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";
import {StringUtils} from "../../../../../../../../utils/string-utils.util";
import {JsonUtil} from "../../../../../../../../utils/json.util";

export class RunConfigTestTreeFilterModel implements Serializable<RunConfigTestTreeFilterModel> {
    showNotExecuted = false;
    showInProgress = false;
    showPassed = false;
    showFailed = false;
    showBlocked = false;
    showNotApplicable = false;
    search: string;
    tags: Array<string> = [];

    static createEmptyFilter() {
        let manualTreeStatusFilterModel = new RunConfigTestTreeFilterModel();
        manualTreeStatusFilterModel.showNotExecuted = true;
        manualTreeStatusFilterModel.showInProgress = true;
        manualTreeStatusFilterModel.showPassed = true;
        manualTreeStatusFilterModel.showFailed = true;
        manualTreeStatusFilterModel.showBlocked = true;
        manualTreeStatusFilterModel.showNotApplicable = true;
        return manualTreeStatusFilterModel;
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

    deserialize(input: Object): RunConfigTestTreeFilterModel {
        this.showNotExecuted = input["showNotExecuted"];
        this.showInProgress = input["showInProgress"];
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

    clone(): RunConfigTestTreeFilterModel {
        let serializedData = this.serialize();
        return new RunConfigTestTreeFilterModel().deserialize(JSON.parse(serializedData));
    }
}
