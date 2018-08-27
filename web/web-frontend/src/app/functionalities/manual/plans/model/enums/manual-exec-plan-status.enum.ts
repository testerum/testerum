import {Enum} from "../../../../../model/enums/enum.interface";

export class ManualExecPlanStatus extends Enum {

    public static IN_EXECUTION = new ManualExecPlanStatus("IN_EXECUTION");
    public static FINISHED = new ManualExecPlanStatus("FINISHED");

    static readonly enums: Array<ManualExecPlanStatus> = [
        ManualExecPlanStatus.IN_EXECUTION,
        ManualExecPlanStatus.FINISHED,
    ];

    private constructor(httpMethodAsString: string) {
        super(httpMethodAsString);
    }

    public static fromString(methodAsString: string) {
        for (let enumValue of ManualExecPlanStatus.enums) {
            if(enumValue.enumAsString == methodAsString) {
                return enumValue;
            }
        }

        throw Error("Enum ["+methodAsString+"] is not defined")
    }
}
