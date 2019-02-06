import {Component} from '@angular/core';
import {FeaturesTreeService} from "./features-tree/features-tree.service";
import {TestsRunnerService} from "./tests-runner/tests-runner.service";

@Component({
    moduleId: module.id,
    selector: 'features',
    templateUrl: 'features.component.html',
    styleUrls: ["features.component.scss"]
})
export class FeaturesComponent {

    constructor(public featuresTreeService: FeaturesTreeService,
                private testsRunnerService: TestsRunnerService) {
    }

    isTestRunnerVisible(): boolean {
        return this.testsRunnerService.isRunnerVisible();
    }
}
