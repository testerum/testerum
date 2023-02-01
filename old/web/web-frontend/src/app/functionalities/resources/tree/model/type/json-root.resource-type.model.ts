
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class JsonRootResourceType implements ResourceType {

   readonly rootFilePath: Path = Path.createInstance("resources/JSON");
   readonly fileExtension: string = null;

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "nf nf-mdi-json";
    resourceUrl: string;
    createSubResourceUrl:any[] = null;

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = false;
    canBeEdited: boolean = false;

    private static instanceForRoot = new JsonRootResourceType();

    static getInstanceForRoot(): ResourceType {
        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return JsonRootResourceType.instanceForRoot;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return null;
    }

    getResourceTypeForChildren(): ResourceType {
        return JsonRootResourceType.getInstanceForChildren();
    }
}
