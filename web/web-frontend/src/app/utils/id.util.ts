
import {ObjectUtil} from "./object.util";

export class IdUtils {

    private static nextTempId:number = (Date.now() + Math.trunc(Math.random() * 10000000000)) * -1;

    public static getTemporaryId():string {
        return ""+IdUtils.nextTempId--;
    }

    public static isTemporaryId(id: string): boolean {
        return ObjectUtil.isANumber(id) && ObjectUtil.getAsNumber(id) < 0
    }
}
