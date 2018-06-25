import {Path} from "../infrastructure/path/path.model";
import {ArgType} from "./arg-type.enum";
import {Resource} from "../resource/resource.model";
import {JsonUtil} from "../../utils/json.util";
import {ServerToUiTypeMapperUtil} from "../../utils/server-to-ui-type-mapper.util";

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

        this.content = ArgType.deserializeInputForServerType(input["content"], this.uiType);
        return this;
    }

    serialize(): string {
        let content: string = this.content.serialize();
        if (!content.startsWith('"')) {
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

}
