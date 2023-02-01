import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {ResourceService} from "../../../service/resources/resource.service";
import {ResourceContext} from "../../../model/resource/resource-context.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ResourceMapEnum} from "./resource-map.enum";
import {StringUtils} from "../../../utils/string-utils.util";

@Injectable()
export class ResourceResolver implements Resolve<any> {

    constructor(private resourceService: ResourceService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let path: Path = route.params['path'] ? Path.createInstance(route.params['path']): Path.createInstanceOfEmptyPath();

        if(route.params['resourceExt']) {
            let newInstanceResource = ResourceMapEnum.getResourceMapEnumByFileExtension(route.params['resourceExt']).getNewInstance();
            return ResourceContext.createInstance(path, newInstanceResource)
        }
        if(path) {
            if (StringUtils.isEmpty(path.fileExtension)) {
                throw new Error("A resource path should be provided")
            }
            let newInstanceResource = ResourceMapEnum.getResourceMapEnumByFileExtension(path.fileExtension).getNewInstance();
            return this.resourceService.getResource(path, newInstanceResource);
        }
        throw new Error("A path variable should be provided")
    }
}
