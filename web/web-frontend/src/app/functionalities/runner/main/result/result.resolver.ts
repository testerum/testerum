import {switchMap} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Params, Resolve} from "@angular/router";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ResultService} from "../../../../service/report/result.service";

@Injectable()
export class ResultResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private resultService: ResultService) {
        this.route.params.pipe(switchMap((params: Params) => this.testId = params['id']));
    }

    resolve(route: ActivatedRouteSnapshot) {
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        return this.resultService.getResult(path);
    }
}
