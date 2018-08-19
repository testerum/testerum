import {Serializable} from "../../../infrastructure/serializable.model";

export class PositionInParent implements Serializable<PositionInParent> {

    id: string;
    indexInParent: number;

    deserialize(input: Object): PositionInParent {
        this.id = input["id"];
        this.indexInParent = input["indexInParent"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}

