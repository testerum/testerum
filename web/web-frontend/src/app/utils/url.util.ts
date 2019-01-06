import {Path} from "../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";

export class UrlUtil {

    public static getPathParamFromUrl(activatedRoute: ActivatedRoute, paramName: string = "path"): Path {
        let pathAsString = activatedRoute.firstChild ? activatedRoute.firstChild.snapshot.params[paramName] : null;
        return pathAsString != null ? Path.createInstance(pathAsString) : null;
    }

    public static getParamFromUrl(activatedRoute: ActivatedRoute, paramName: string): string {
        if (!paramName) {
            return null;
        }

        return activatedRoute.firstChild ? activatedRoute.firstChild.snapshot.params[paramName] : null;
    }
}
