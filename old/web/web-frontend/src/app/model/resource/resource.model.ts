import {Serializable} from "../infrastructure/serializable.model";

export interface Resource<T> extends Serializable<T> {
    deserialize(input: Object):T;
    serialize():string;
    clone(): T;
    createInstance():T;
    isEmpty(): boolean;

    //This method should set the object properties as an empty instance
    reset(): void;
}
