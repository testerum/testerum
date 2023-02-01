import {Serializable} from "../../../infrastructure/serializable.model";

export interface TypeMeta extends Serializable<any>{
    javaType: string

}
