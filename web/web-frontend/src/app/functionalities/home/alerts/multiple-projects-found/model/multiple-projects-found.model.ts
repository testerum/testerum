import {Project} from "../../../../../model/home/project.model";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../../../utils/json.util";

export class MultipleProjectsFound implements Serializable<MultipleProjectsFound>{
    urlToNavigate: string;
    projects: Array<Project> = [];

    deserialize(input: Object): MultipleProjectsFound {
        this.urlToNavigate = input['urlToNavigate'];
        for (let project of (input['projects'] || [])) {
            this.projects.push(Project.deserialize(project));
        }
        return this;
    }

    serialize(): string {
        return '' +
            '{' +
            '"urlToNavigate":'+JsonUtil.stringify(this.urlToNavigate) +
            ',"projects":'+JsonUtil.serializeArrayOfSerializable(this.projects) +
            '}';
    }
}
