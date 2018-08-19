import {Serializable} from "./serializable.model";

export interface SerializableUnknown<T> extends Serializable<T> {

    canDeserialize(input: any): boolean;

}
