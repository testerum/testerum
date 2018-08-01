import {switchMap} from 'rxjs/operators';
import {Injectable} from "@angular/core";
import {ActivatedRoute, ActivatedRouteSnapshot, Params, Resolve} from "@angular/router";
import {ManualTestsRunnerService} from "../../runner/service/manual-tests-runner.service";

@Injectable()
export class ManualTestsExecutorEditorResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private manualTestsRunnerService: ManualTestsRunnerService) {
        this.route.params.pipe(switchMap((params: Params) => this.testId = params['id']));
    }

    resolve(route: ActivatedRouteSnapshot) {

        let runnerPathAsString = route.params['runnerPath'];

        return this.manualTestsRunnerService.getTestRunner(runnerPathAsString);
    }
}
