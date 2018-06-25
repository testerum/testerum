
interface Serializable<T> {
    deserialize(input: Object):T;
    serialize():string;
}