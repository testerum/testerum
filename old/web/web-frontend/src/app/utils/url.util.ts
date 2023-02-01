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

        let param = activatedRoute.snapshot.params[paramName];
        if (param) {
            return param
        }

        return activatedRoute.firstChild ? activatedRoute.firstChild.snapshot.params[paramName] : null;
    }

    public static getProjectNameFromActivatedRoute(activatedRoute: ActivatedRoute): string {
        return activatedRoute.snapshot.url.length ? activatedRoute.snapshot.url[0].path : null;
    }

    public static getProjectNameFromUrl(url: string): string {
        let urlParts = url.split("/").filter(value => value.length > 0);
        return urlParts.length > 0 ? urlParts[0] : null;
    }
}
