
import {ResourceType} from "./resource-type.model";
import {PathUtil} from "../../../../../utils/path.util";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class RdbmsSqlResourceType implements ResourceType {

   readonly rootFilePath: Path = Path.createInstance("resources/RDBMS/SQL");
   readonly fileExtension: string = "sql";

   readonly name:string = this.rootFilePath.getLastPathPart();
   readonly iconClass: string = "fas fa-table";
   readonly resourceUrl: string = "/automated/resources/rdbms/sql/";
   readonly createSubResourceUrl:any[] = ["/automated/resources/rdbms/sql/create", {"path": this.rootFilePath}];

   readonly canHaveChildrenContainers: boolean = true;
   readonly canBeDeleted: boolean = true;
   readonly canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;
    static getInstanceForRoot(): ResourceType {
        if (!RdbmsSqlResourceType.instanceForRoot) {
            this.instanceForRoot = new RdbmsSqlResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new RdbmsSqlResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return RdbmsSqlResourceType.getInstanceForChildren();
    }
}
