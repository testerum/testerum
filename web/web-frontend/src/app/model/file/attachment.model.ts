import {Path} from "../infrastructure/path/path.model";
import {JsonUtil} from "../../utils/json.util";

export class Attachment implements Serializable<Attachment> {
    path:Path;
    mimeType: string;
    size: number;
    lastModifiedDate: Date;

    deserialize(input: Object): Attachment {
        this.path = Path.deserialize(input["path"]);
        this.mimeType = input["mimeType"];
        this.size = input["size"];
        this.lastModifiedDate = new Date(input["lastModifiedDate"]);

        return this;
    }

    serialize() {
        return ""+
            '{' +
            '"path":'+ JsonUtil.serializeSerializable(this.path)+
            ',"mimeType":' + JsonUtil.stringify(this.mimeType) +
            ',"size":' + JsonUtil.stringify(this.size) +
            ',"lastModifiedDate":' + JsonUtil.stringify(this.lastModifiedDate?this.lastModifiedDate.toJSON(): null) +
            '}'
    }
}
