import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {Path} from "../../../model/infrastructure/path/path.model";
import {FeatureService} from "../../../service/feature.service";
import {Feature} from "../../../model/feature/feature.model";

@Injectable()
export class FeatureResolver implements Resolve<any> {

    constructor(private route: ActivatedRoute,
                private featureService: FeatureService) {
    }

    resolve(route: ActivatedRouteSnapshot) {
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        let actionParam = route.params["action"];
        if (actionParam == "create") {
            let feature = new Feature();
            feature.path = path;
            return feature
        }

        return this.featureService.getFeature(pathAsString);
    }
}
