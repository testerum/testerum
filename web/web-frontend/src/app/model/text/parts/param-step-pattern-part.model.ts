import {StepPatternPart} from "./step-pattern-part.model";
import {JsonUtil} from "../../../utils/json.util";
import {ServerToUiTypeMapperUtil} from "../../../utils/server-to-ui-type-mapper.util";
import {Serializable} from "../../infrastructure/serializable.model";
import {TypeMeta} from "./param-meta/type-meta.model";
import {FieldTypeMeta} from "./param-meta/field/field-type-meta.model";
import {ResourceMapEnum} from "../../../functionalities/resources/editors/resource-map.enum";

export class ParamStepPatternPart implements StepPatternPart, Serializable<ParamStepPatternPart>{

    name: string;
    serverType: TypeMeta;
    uiType: string;
    description: string;
    enumValues: Array<string> = [];

    deserialize(input: Object): ParamStepPatternPart {
        this.name = input["name"];
        this.serverType = FieldTypeMeta.deserializeTypeMeta(input["typeMeta"]);
        this.uiType = ResourceMapEnum.getResourceMapEnumByTypeMeta(this.serverType).uiType;
        this.description = input["description"];

        for (let allowedValuesJson of input["enumValues"] || []) {
            this.enumValues.push(allowedValuesJson);
        }

        return this;
    }

    serialize(): string {
        return ""+
            '{' +
            '"@type": "PARAM",'+
            '"name":'+JsonUtil.stringify(this.name) +','+
            '"typeMeta":'+JsonUtil.serializeSerializable(this.serverType) +','+
            '"description":'+JsonUtil.stringify(this.description) +','+
            '"enumValues":'+JsonUtil.serializeArray(this.enumValues)+
            '}'
    }
}
