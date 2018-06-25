import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {TestsService} from "../../../service/tests.service";
import {TestModel} from "../../../model/test/test.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ComposedStepDef} from "../../../model/composed-step-def.model";

@Injectable()
export class TestResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private testsService: TestsService) {
        this.route.params.switchMap((params: Params) => this.testId = params['id']);
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : new Path([], null, null);

        if (action == "create") {
            let testModel = new TestModel();
            testModel.path = path;

            return testModel;
        }

        return this.testsService.getTest(pathAsString);
    }
}
