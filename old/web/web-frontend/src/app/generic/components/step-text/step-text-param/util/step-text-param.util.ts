import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {StringUtils} from "../../../../../utils/string-utils.util";
import {Arg} from "../../../../../model/arg/arg.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {ObjectResourceModel} from "../../../../../functionalities/resources/editors/object/object-resource.model";

export class StepTextParamUtil {


    static hasValue(arg:Arg): boolean {
        if (arg.content instanceof BasicResource || arg.content instanceof ObjectResourceModel) {
            return arg.content.content != null
                && (typeof arg.content.content == "string" && !StringUtils.isEmpty(arg.content.content));
        }else
        if (arg.content) {
            return true;
        }
        return false
    }

    static getArgumentName(arg:Arg, paramStepPart:ParamStepPatternPart): string {
        if (arg.name) {
            return arg.name
        }

        if (paramStepPart.name) {
            return paramStepPart.name
        }

        if (arg.path && arg.path.fileName) {
            return arg.path.fileName;
        }

        return "param";
    }

    static getArgumentValue(arg:Arg): string {

        if (arg.content instanceof BasicResource) {
            if(typeof arg.content.content === "string" || typeof arg.content.content === "number" ) {
                if (arg.path && arg.path.fileName) {
                    return arg.path.fileName;
                }

                if (!StringUtils.isEmpty(arg.name) && arg.name != arg.paramName) {
                    return arg.name;
                }

                if (!arg.content.isSmallText()) {
                    let fullValue = arg.content.content as string;
                    let result = fullValue.split('\n')[0];
                    result = result.substring(0, BasicResource.SMALL_TEXT_LENGTH);
                    return result + "...";
                }
                return arg.content.content;
            }
        }

        if (arg.name) {
            return arg.name
        }

        if (arg.path && arg.path.fileName) {
            return arg.path.fileName;
        }

        return "";
    }
}
