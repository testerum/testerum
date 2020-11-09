import {Serializable} from "../../infrastructure/serializable.model";
import {StepCall} from "../../step/step-call.model";
import {Path} from "../../infrastructure/path/path.model";

export class Hooks implements Serializable<Hooks> {
    beforeAll: Array<StepCall> = [];
    beforeEach: Array<StepCall> = [];
    afterEach: Array<StepCall> = [];
    afterAll: Array<StepCall> = [];

    parentEntityPath: Path;

    constructor(parentEntityPath: Path) {
        this.parentEntityPath = parentEntityPath;
    }

    deserialize(input: Object): Hooks {

        return this;
    }

    serialize(): string {
        return "";
    }
}
