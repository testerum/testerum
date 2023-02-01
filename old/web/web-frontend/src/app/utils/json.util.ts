import {Serializable} from "../model/infrastructure/serializable.model";
import {StringUtils} from "./string-utils.util";

export class JsonUtil {
    static readonly TESTERUM_EXPRESION_PATTERN = /{{(.*?)(?=}})}}/g;

    public static serializeDateWithoutTime(date: Date): string {
        if (date) {
            // format is "2019-04-02T15:19:21.995Z"
            // we are keeping only the date part
            const dateTimeAsString = date.toJSON();
            const dateAsString = dateTimeAsString.substring(0, 10);

            return JSON.stringify(dateAsString);
        } else {
            return "null";
        }
    }

    public static serializeSerializable(serializable: Serializable<any>): string {
        return serializable ? serializable.serialize() : "null";
    }

    public static serializeArrayOfSerializable(array: Array<Serializable<any>>): string {
        let result: string = "[";
        array.forEach((item, index) => {
            if (index != 0) {
                result += ','
            }
            result += item.serialize()
        });
        result += "]";
        return result
    }

    public static serializeArray(array: Array<any>): string {
        let result: string = "[";
        array.forEach((item, index) => {
            if (index != 0) {
                result += ','
            }
            result += JSON.stringify(item)
        });
        result += "]";
        return result
    }

    public static stringify(jsonValue: any): string {
        return JSON.stringify(
            jsonValue != null ? jsonValue : null
        )
    }

    static serializeMap(map: Map<string, string>): string {
        if (!map) {
            return "null";
        }
        return JSON.stringify(JsonUtil.mapToObj(map));
    }
    private static mapToObj(map: Map<string, string>): object {
        let obj = Object.create(null);
        map.forEach((value, key) => {
            obj[key] = value;
        });
        return obj;
    }

    public static isJson(str: string) {
        let strWithoutTesterumExpressions = str ? str.replace(JsonUtil.TESTERUM_EXPRESION_PATTERN, "2") : str;
        try {
            JSON.parse(strWithoutTesterumExpressions);
        } catch (e) {
            return false;
        }
        return true;
    }

    public static parseJson(str: string) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSON.parse(str);
    }
}
