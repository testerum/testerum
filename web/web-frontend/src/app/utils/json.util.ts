import {Serializable} from "../model/infrastructure/serializable.model";

export class JsonUtil {

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

    public static isJson(str: string) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    }

    public static parseJson(str: string) {
        if (!str) {
            return null;
        }
        return JSON.parse(str);
    }
}
