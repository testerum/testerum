import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {ManualTestsRunnerService} from "../service/manual-tests-runner.service";

@Injectable()
export class ManualTestsRunnerEditorResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private manualTestsRunnerService: ManualTestsRunnerService) {
        this.route.params.switchMap((params: Params) => this.testId = params['id']);
    }

    resolve(route: ActivatedRouteSnapshot) {
        let action = route.params['action'];
        let pathAsString = route.params['path'];
        let path = pathAsString ? Path.createInstance(pathAsString) : Path.createInstanceOfEmptyPath();

        if (action == "create") {
            let manualTestRunner = new ManualTestsRunner();
            manualTestRunner.path = path;

            return manualTestRunner;
        }

        return this.manualTestsRunnerService.getTestRunner(pathAsString);
    }
}
