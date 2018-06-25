
export interface SerializableUnknown<T> extends Serializable<T> {

    canDeserialize(input: any): boolean;

}
