import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestsService} from "../service/manual-tests.service";
import {ManualTestModel} from "../../model/manual-test.model";

@Injectable()
export class ManualTestResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private manualTestsService: ManualTestsService) {
        this.route.params.switchMap((params: Params) => this.testId = params['id']);
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        if (action == "create") {
            let testModel = new ManualTestModel();
            testModel.path = path;

            return testModel;
        }

        return this.manualTestsService.getTest(pathAsString);
    }
}
