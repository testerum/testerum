
import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockResponseType extends Enum {

    public static MOCK = new HttpMockResponseType("MOCK");
    public static FAULT = new HttpMockResponseType("FAULT");
    public static PROXY = new HttpMockResponseType("PROXY");

    static readonly enums: Array<HttpMockResponseType> = [
        HttpMockResponseType.MOCK,
        HttpMockResponseType.FAULT,
        HttpMockResponseType.PROXY
    ];

    private constructor(asString: string) {
        super(asString);
    }

    public static fromString(asString: string): HttpMockResponseType {
        for (let enumValue of HttpMockResponseType.enums) {
            if(enumValue.enumAsString.toUpperCase() == asString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
