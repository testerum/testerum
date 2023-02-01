import {Path} from "../../../../../model/infrastructure/path/path.model";

export abstract class ResourceType {

    rootFilePath: Path;
    fileExtension: string;

    name: string;
    iconClass: string;
    resourceUrl: string;
    createSubResourceUrl: any[]; //route configuration

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = false;
    canBeEdited: boolean = false;

    abstract getCreateSubResourceUrl(path: Path): any[];
    abstract getResourceTypeForChildren(): ResourceType;
}
