import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {ResourceService} from "../../../../service/resources/resource.service";
import {ResourceContext} from "../../../../model/resource/resource-context.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {EmptyJsonVerify} from "./json-verify-tree/model/empty-json-verify.model";
import {SerializationUtil} from "./json-verify-tree/model/util/serialization.util";

@Injectable()
export class JsonVerifyResolver implements Resolve<any> {

    constructor(private resourceService: ResourceService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let path: string = route.params['path'];
        if(!path) {
           path = "";
        }

        if(route.params['create'] == "create") {
           return ResourceContext.createInstance(Path.createInstance(path), new EmptyJsonVerify())
        }
        if(path) {
            return this.resourceService.getResource(Path.createInstance(path), new SerializationUtil());
        }
        throw new Error("A path variable should be provided")
    }
}
