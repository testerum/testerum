import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ResultService} from "../../../../service/report/result.service";

@Injectable()
export class ResultResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private resultService: ResultService) {
        this.route.params.switchMap((params: Params) => this.testId = params['id']);
    }

    resolve(route: ActivatedRouteSnapshot) {
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        return this.resultService.getResult(path);
    }
}
