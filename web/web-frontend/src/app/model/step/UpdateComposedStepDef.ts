import {Path} from "../infrastructure/path/path.model";
import {ComposedStepDef} from "../composed-step-def.model";
import {JsonUtil} from "../../utils/json.util";
import {Serializable} from "../infrastructure/serializable.model";

export class UpdateComposedStepDef implements Serializable<UpdateComposedStepDef> {

    oldPath: Path;
    composedStepDef: ComposedStepDef;

    constructor(oldPath: Path, composedStepDef: ComposedStepDef) {
        this.oldPath = oldPath;
        this.composedStepDef = composedStepDef;
    }

    deserialize(input: Object): UpdateComposedStepDef {
        throw new Error("Unimplemented method");
    }

    serialize(): string {
        return "" +
            '{' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"composedStepDef":' + JsonUtil.serializeSerializable(this.composedStepDef) +
            '}'
    }
}
