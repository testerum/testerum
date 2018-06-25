import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, Params, ActivatedRoute} from "@angular/router";
import {ManualTestsRunnerService} from "../runner/service/manual-tests-runner.service";

@Injectable()
export class ManualTestsExecutorResolver implements Resolve<any> {

    private testId:string;
    constructor(private route: ActivatedRoute,
                private manualTestsRunnerService: ManualTestsRunnerService) {
        this.route.params.switchMap((params: Params) => this.testId = params['id']);
    }

    resolve(route: ActivatedRouteSnapshot) {
        let runnerPathAsString = route.params['runnerPath'];

        if(!runnerPathAsString) return null;

        return this.manualTestsRunnerService.getTestRunner(runnerPathAsString);
    }
}
