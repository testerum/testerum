import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class RdbmsConnectionResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/RDBMS/Connections");
    readonly fileExtension: string = "rdbms.connection.yaml";

     name: string = this.rootFilePath.getLastPathPart();
     iconClass: string = "fas fa-plug";
     resourceUrl: string = "/resources/rdbms/connection/";
     createSubResourceUrl = ["/resources/rdbms/connection/create", {"path": this.rootFilePath}];

     canHaveChildrenContainers: boolean = true;
     canBeDeleted: boolean = true;
     canBeEdited: boolean = true;


    private static instanceForRoot: ResourceType;

    static getInstanceForRoot(): ResourceType {
        if (!RdbmsConnectionResourceType.instanceForRoot) {
            this.instanceForRoot = new RdbmsConnectionResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new RdbmsConnectionResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return RdbmsConnectionResourceType.getInstanceForChildren();
    }
}
