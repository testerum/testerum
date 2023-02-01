import {StringUtils} from "./string-utils.util";
import {StepPhaseEnum, StepPhaseUtil} from "../model/enums/step-phase.enum";
import {Path} from "../model/infrastructure/path/path.model";

export class PathUtil {

    public static normalizePartPath(partPath: string): string {
        return partPath.replace(/([^\w-])/gi, '_');
    }

    public static normalizeMultiPartPath(multiPartPath: string): string {
        return multiPartPath.replace(/([^\w-/])/gi, '_');
    }

    public static getFileNameWithoutExtension(path: string): string {
        let startIndex = path.lastIndexOf("/") + 1;
        if (startIndex < 0) {
            startIndex = 0;
        }

        let endIndex = path.indexOf(".");
        if (endIndex < 0) {
            return null;
        }

        return path.substring(startIndex, endIndex);
    }

    static getLastPathPart(path: string): string {
        let startIndex = path.lastIndexOf("/") + 1;
        if (startIndex < 0) {
            startIndex = 0;
        }

        return path.substring(startIndex, path.length);
    }

    static pathPartAsName(pathPart: string): string {
        if(!pathPart) {
            return pathPart
        }

        return pathPart.toLowerCase().replace(/_(.)/g, function(match, group1) {
            return " "+group1;
        });
    }

    static pathPartAsUperCaseName(pathPart: string): string {
        if(!pathPart) {
            return pathPart
        }

        return pathPart.toLowerCase().replace(/_(.)/g, function(match, group1) {
            return group1.toUpperCase();
        });
    }

    static generateStepDefPath(containerPath: Path, stepPhase: StepPhaseEnum, stepTextWithoutPhase: string): Path {
        let stepFileName = StepPhaseUtil.toCamelCaseString(stepPhase) + " " + stepTextWithoutPhase;
        return new Path(containerPath.directories, stepFileName, "step");
    }
}
