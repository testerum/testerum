import {Path} from "../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";

export class UrlUtil {

    public static getPathParamFromUrl(activatedRoute: ActivatedRoute): Path {
        let pathAsString = activatedRoute.firstChild ? activatedRoute.firstChild.snapshot.params['path'] : null;
        return pathAsString != null ? Path.createInstance(pathAsString) : null;
    }
}
