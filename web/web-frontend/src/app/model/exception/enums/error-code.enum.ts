import {Enum} from "../../enums/enum.interface";

export class ErrorCode extends Enum {
    public static GENERIC_ERROR = new ErrorCode("GENERIC_ERROR");
    public static VALIDATION = new ErrorCode("VALIDATION");
    public static ILLEGAL_FILE_OPERATION = new ErrorCode("ILLEGAL_FILE_OPERATION");
    public static CLOUD_ERROR = new ErrorCode("CLOUD_ERROR");
    public static CLOUD_OFFLINE = new ErrorCode("CLOUD_OFFLINE");
    public static INVALID_CREDENTIALS = new ErrorCode("INVALID_CREDENTIALS");

    static readonly enums: Array<ErrorCode> = [
        ErrorCode.GENERIC_ERROR,
        ErrorCode.VALIDATION,
        ErrorCode.ILLEGAL_FILE_OPERATION,
        ErrorCode.CLOUD_ERROR,
        ErrorCode.CLOUD_OFFLINE,
        ErrorCode.INVALID_CREDENTIALS,
    ];

    private constructor(asString: string) {
        super(asString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ErrorCode.enums) {
            if (enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum [" + methodAsString + "] is not defined")
    }
}
