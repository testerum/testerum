import {Path} from "../infrastructure/path/path.model";
import {Resource} from "../resource/resource.model";
import {JsonUtil} from "../../utils/json.util";
import {ServerToUiTypeMapperUtil} from "../../utils/server-to-ui-type-mapper.util";
import {ParamStepPatternPart} from "../text/parts/param-step-pattern-part.model";
import {ResourceMapEnum} from "../../functionalities/resources/editors/resource-map.enum";

export class Arg implements Serializable<Arg> {

    name: string;

    content:Resource<any>;
    serverType: string;
    uiType: string;
    path: Path;

    deserialize(input: Object): Arg {
        this.name = input["name"];
        this.serverType = input["type"];
        this.uiType = ServerToUiTypeMapperUtil.mapServerToUi(this.serverType);
        if(input["path"]) {
            this.path = Path.deserialize(input["path"]);
        }

        this.content = ResourceMapEnum.deserializeInputForUiType(input["content"], this.uiType);
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
            ',"type":' + JsonUtil.stringify(this.uiType) +
            ',"path":' + JsonUtil.stringify(this.path) +
            ',"content":' + content +
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
