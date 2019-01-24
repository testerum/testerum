import {Serializable} from "../infrastructure/serializable.model";
import {Project} from "./project.model";
import {JsonUtil} from "../../utils/json.util";
import {Quote} from "./quote.model";

export class Home implements Serializable<Home>  {

    quote: Quote;
    testerumVersion: string;
    recentProjects: Project[] = [];

    deserialize(input: Object): Home {
        this.quote = new Quote().deserialize(input["quote"]);
        this.testerumVersion = input["testerumVersion"];

        for (let recentProject of (input['recentProjects']) || []) {
            this.recentProjects.push(Project.deserialize(recentProject));
        }

        return this;
    }

    serialize(): string {
        let response = "" +
            '{' +
            '"quote":' + JsonUtil.stringify(this.quote) + ',' +
            '"testerumVersion":' + JsonUtil.stringify(this.testerumVersion) + ',';

        if (this.recentProjects) {
            response += '"recentProjects":' + JsonUtil.serializeArrayOfSerializable(this.recentProjects);
        }

        response += '}';

        return response;
    }
}
