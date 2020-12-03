import {Enum} from "../../../../../../model/enums/enum.interface";

export class StepUsageDialogModeEnum extends Enum {
    public static DELETE_STEP = new StepUsageDialogModeEnum("DELETE_STEP");
    public static UPDATE_STEP = new StepUsageDialogModeEnum("UPDATE_STEP");
    public static INFO_STEP = new StepUsageDialogModeEnum("INFO_STEP");


    private constructor(enumAsString:string) {
        super(enumAsString);
    }
}
