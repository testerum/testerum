
import {ResourceType} from "./resource-type.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";

export class RdbmsVerifyResourceType implements ResourceType {

    readonly rootFilePath: Path = Path.createInstance("resources/RDBMS/Verify");
    readonly fileExtension: string = "rdbms.verify.json";

    name:string = this.rootFilePath.getLastPathPart();
    iconClass: string = "nf nf-fa-check_square_o";
    resourceUrl: string = "/automated/resources/rdbms/verify/";
    createSubResourceUrl:any[] = ["/automated/resources/rdbms/verify/create", {"path": this.rootFilePath}];

    canHaveChildrenContainers: boolean = true;
    canBeDeleted: boolean = true;
    canBeEdited: boolean = true;

    private static instanceForRoot: ResourceType;
    static getInstanceForRoot(): ResourceType {
        if (!RdbmsVerifyResourceType.instanceForRoot) {
            this.instanceForRoot = new RdbmsVerifyResourceType();
            this.instanceForRoot.canBeEdited = false;
            this.instanceForRoot.canBeDeleted = false;
        }

        return this.instanceForRoot;
    }

    static getInstanceForChildren(): ResourceType {
        return new RdbmsVerifyResourceType();
    }

    getCreateSubResourceUrl(path: Path): any[] {
        return [this.createSubResourceUrl[0], {"path": path}]
    }

    getResourceTypeForChildren(): ResourceType {
        return RdbmsVerifyResourceType.getInstanceForChildren();
    }
}
