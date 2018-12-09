import {Path} from "../infrastructure/path/path.model";

export class Project {

    readonly name: string;
    readonly path: Path;

    constructor(name: string, path: Path) {
        this.name = name;
        this.path = path;
    }
}
