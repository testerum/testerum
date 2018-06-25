
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class RdbmsRootResourceType implements ResourceType {

   readonly rootFilePath: Path = Path.createInstance("resources/RDBMS");
   readonly fileExtension: string = null;

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "fa fa-database";
    resourceUrl: string;
    createSubResourceUrl:any[] = null;

    canHaveChildrenContainers: boolean = false;
    canBeDeleted: boolean = false;
    canBeEdited: boolean = false;

    private static instanceForRoot = new RdbmsRootResourceType();

    static getInstanceForRoot(): ResourceType {
        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return RdbmsRootResourceType.instanceForRoot;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return null;
    }

    getResourceTypeForChildren(): ResourceType {
        return RdbmsRootResourceType.getInstanceForChildren();
    }
}
