
import {JsonUtil} from "../../utils/json.util";
import {Serializable} from "../infrastructure/serializable.model";

export class CompareMode implements Serializable<CompareMode>{
    static PROPERTY_NAME_IN_JSON: string = "=compareMode";

    private text: string;
    textForView: string;

    private constructor(text:string, textForView:string) {
        this.text = text;
        this.textForView = textForView;
    }

    static INHERIT = new CompareMode("inherit", "inherit");
    static CONTAINS = new CompareMode("contains", "contains");
    static DOES_NOT_CONTAINS = new CompareMode("doesNotContain", "does not contain");
    static EXACT = new CompareMode("exact", "exact");

    static getAllCompareModes(): Array<CompareMode> {
        return [CompareMode.INHERIT, CompareMode.CONTAINS, CompareMode.DOES_NOT_CONTAINS, CompareMode.EXACT]
    }

    static getByText(mode: string): CompareMode  {
        switch (mode) {
            case CompareMode.INHERIT.text: return CompareMode.INHERIT;
            case CompareMode.CONTAINS.text: return CompareMode.CONTAINS;
            case CompareMode.DOES_NOT_CONTAINS.text: return CompareMode.DOES_NOT_CONTAINS;
            case CompareMode.EXACT.text: return CompareMode.EXACT;
        }
        throw new Error("Unknown Verify Mode");
    }

    getText(): string {
        return this.text;
    }

    callDeserialize(input: Object): CompareMode {
        return new CompareMode("", "").deserialize(input)
    }

    deserialize(input: Object): CompareMode {
        return CompareMode.getByText(input[CompareMode.PROPERTY_NAME_IN_JSON])
    }

    serialize(): string {
        return JsonUtil.stringify(this.text)
    }

    static deserializeFromJsonString(tableRow: string): CompareMode {
        let strings = tableRow.split(":");
        if(strings.length != 2 || strings[0].trim() != '=compareMode') {
            throw Error("The ["+tableRow+"] is not a valid Compare Mode String. Ex of valid Compare Mode = ['=compareMode: exact']")
        }

        return CompareMode.getByText(strings[1].trim());
    }
}
