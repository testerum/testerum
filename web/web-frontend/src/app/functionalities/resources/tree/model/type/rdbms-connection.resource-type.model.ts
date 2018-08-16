import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class RdbmsConnectionResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/RDBMS/Connections");
    readonly fileExtension: string = "rdbms.connection.yaml";

    readonly name: string = this.rootFilePath.getLastPathPart();
    readonly iconClass: string = "fas fa-plug";
    readonly resourceUrl: string = "/automated/resources/rdbms/connection/";
    readonly createSubResourceUrl = ["/automated/resources/rdbms/connection/create", {"path": this.rootFilePath}];

    readonly canHaveChildrenContainers: boolean = false;
    readonly canBeDeleted: boolean = false;
    readonly canBeEdited: boolean = false;


    private static instance = new RdbmsConnectionResourceType();

    static getInstanceForRoot(): ResourceType {
        return this.instance;
    }

    static getInstanceForChildren(): ResourceType {
        return this.instance;
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return RdbmsConnectionResourceType.getInstanceForChildren();
    }
}
