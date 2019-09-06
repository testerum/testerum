import {Path} from "../infrastructure/path/path.model";
import {Resource} from "../resource/resource.model";
import {JsonUtil} from "../../utils/json.util";
import {ServerToUiTypeMapperUtil} from "../../utils/server-to-ui-type-mapper.util";
import {ParamStepPatternPart} from "../text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../functionalities/resources/editors/resource-map.enum";
import {Warning} from "../warning/Warning";
import {BasicResourceComponent} from "../../functionalities/resources/editors/basic/basic-resource.component";
import {Serializable} from "../infrastructure/serializable.model";
import {TypeMeta} from "../text/parts/param-meta/type-meta.model";
import {FieldTypeMeta} from "../text/parts/param-meta/field/field-type-meta.model";

export class Arg implements Serializable<Arg> {

    name: string;

    content:Resource<any>;
    serverType: TypeMeta;
    uiType: string;
    path: Path;
    oldPath: Path;

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    paramName: string; //this param should not be serialized

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    deserialize(input: Object): Arg {
        this.name = input["name"];
        this.serverType = FieldTypeMeta.deserializeTypeMeta(input["typeMeta"]);
        this.uiType = ServerToUiTypeMapperUtil.mapServerToUi(this.serverType.javaType);
        if (input["path"]) {
            this.path = Path.deserialize(input["path"]);
        }
        if (input["oldPath"]) {
            this.oldPath = Path.deserialize(input["oldPath"]);
        }

        this.content = ResourceMapEnum.deserializeInputForUiType(input["content"], this.uiType);

        this.warnings = [];
        for (let warning of (input['warnings'] || [])) {
            this.warnings.push(
                new Warning().deserialize(warning)
            );
        }

        this.descendantsHaveWarnings = input['descendantsHaveWarnings'];

        return this;
    }

    serialize(): string {
        let content: string = this.content.serialize();

        if (typeof content === 'undefined') {
            content = null;
        }

        if (content != null && !content.startsWith('"')) {
            content = JsonUtil.stringify(content);
        }

        return ""+
            '{' +
            '"name":' + JsonUtil.stringify(this.name) +
            ',"typeMeta":' + JsonUtil.serializeSerializable(this.serverType) +
            ',"path":' + JsonUtil.stringify(this.path) +
            ',"oldPath":' + JsonUtil.stringify(this.oldPath) +
            ',"content":' + content +
            ',"warnings": []' +
            '}';
    }

    public static createArgInstanceFromParamStepPatternPart(paramPart:ParamStepPatternPart): Arg {
        let arg: Arg = new Arg();

        arg.name = paramPart.name;
        arg.serverType = paramPart.serverType;
        arg.uiType = paramPart.uiType;
        arg.content = null;
        let resourceMapEnumByUiType = ResourceMapEnum.getResourceMapEnumByUiType(paramPart.uiType);
        if (resourceMapEnumByUiType.resourceTypeInstanceForChildrenFunction) {
            arg.content = resourceMapEnumByUiType.getNewInstance()
        }

        return arg;
    }
}
