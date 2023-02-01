import {Serializable} from "../infrastructure/serializable.model";
import {JsonUtil} from "../../utils/json.util";

export class CreateProjectRequest implements Serializable<CreateProjectRequest> {

    projectParentDir: string;
    projectName: string;

    static create(projectParentDir: string, projectName: string): CreateProjectRequest {
        const createProjectRequest = new CreateProjectRequest();

        createProjectRequest.projectParentDir = projectParentDir;
        createProjectRequest.projectName = projectName;

        return createProjectRequest;
    }

    deserialize(input: Object): CreateProjectRequest {
        this.projectParentDir = input["projectParentDir"];
        this.projectName = input["projectName"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"projectParentDir":' + JsonUtil.stringify(this.projectParentDir) + ',' +
            '"projectName":' + JsonUtil.stringify(this.projectName) +
            '}';
    }

}
