import {StringUtils} from "../../string-utils.util";

export class StepSearchTokenizationUtil {

    static tokenize(stepText: string): Array<string> {
        let result = [];
        let rowTokens = stepText.split((/[\s\,\.\:\;\<\>\[\]\{\}\&\!\#\$\(\)\|\"\'\-\_\+\=\*]+/));
        for (const rowToken of rowTokens) {
            if (StringUtils.isNotEmpty(rowToken)) {
                result.push(rowToken.toLowerCase())
            }
        }
        return result;
    }
}
