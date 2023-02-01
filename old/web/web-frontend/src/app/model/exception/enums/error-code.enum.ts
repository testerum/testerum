import {Enum} from "../../enums/enum.interface";

export class ErrorCode extends Enum {

    public static readonly GENERIC_ERROR          = new ErrorCode("GENERIC_ERROR");
    public static readonly VALIDATION             = new ErrorCode("VALIDATION");
    public static readonly ILLEGAL_FILE_OPERATION = new ErrorCode("ILLEGAL_FILE_OPERATION");
    public static readonly CLOUD_ERROR            = new ErrorCode("CLOUD_ERROR");
    public static readonly CLOUD_OFFLINE          = new ErrorCode("CLOUD_OFFLINE");
    public static readonly INVALID_CREDENTIALS    = new ErrorCode("INVALID_CREDENTIALS"); // invalid username/password combination
    public static readonly NO_VALID_LICENSE       = new ErrorCode("NO_VALID_LICENSE");    // the user exists, but he/she doesn't have any valid license assigned

    static readonly enums: Array<ErrorCode> = [
        ErrorCode.GENERIC_ERROR,
        ErrorCode.VALIDATION,
        ErrorCode.ILLEGAL_FILE_OPERATION,
        ErrorCode.CLOUD_ERROR,
        ErrorCode.CLOUD_OFFLINE,
        ErrorCode.INVALID_CREDENTIALS,
        ErrorCode.NO_VALID_LICENSE,
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
