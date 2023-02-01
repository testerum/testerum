import {Enum} from "../../../../model/enums/enum.interface";

export class JsonCompareModeEnum extends Enum {

    public static EXACT = new JsonCompareModeEnum("exact");
    public static UNORDERED_EXACT = new JsonCompareModeEnum("unorderedExact");
    public static CONTAINS = new JsonCompareModeEnum("contains");

    static readonly enums: Array<JsonCompareModeEnum> = [
        JsonCompareModeEnum.EXACT,
        JsonCompareModeEnum.UNORDERED_EXACT,
        JsonCompareModeEnum.CONTAINS
    ];

    private constructor(compareModeAsString:string) {
        super(compareModeAsString);
    }

    public static fromString(compareModeAsString: string) {
        for (let enumValue of JsonCompareModeEnum.enums) {
            if(enumValue.enumAsString == compareModeAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+compareModeAsString+"] is not defined")
    }
}
