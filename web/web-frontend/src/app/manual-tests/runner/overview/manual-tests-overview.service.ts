import {Injectable} from "@angular/core";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {ManualTestsRunnerService} from "../service/manual-tests-runner.service";
import {ManualTestsRunnerStatus} from "../model/enums/manual-tests-runner-status.enum";

@Injectable()
export class ManualTestsOverviewService {

    activeRunners: Array<ManualTestsRunner> = [];
    finalizedRunners: Array<ManualTestsRunner> = [];

    constructor(private manualTestsRunnerService: ManualTestsRunnerService) {
    }

    initializeRunnersOverview() {
        this.activeRunners.length = 0;
        this.finalizedRunners.length = 0;

        this.manualTestsRunnerService.getTests().subscribe(
            (runners: Array<ManualTestsRunner>) => {
                for (const runner of runners) {
                    if (runner.status == ManualTestsRunnerStatus.IN_EXECUTION) {
                        this.activeRunners.push(runner);
                    }
                    if (runner.status == ManualTestsRunnerStatus.FINISHED) {
                        this.finalizedRunners.push(runner);
                    }
                }

                this.sortByCreateDate(this.activeRunners);
                this.sortByFinalizedDate(this.finalizedRunners);
            }
        )
    }

    sortByCreateDate(runners: Array<ManualTestsRunner>) {
        runners.sort((left: ManualTestsRunner, right: ManualTestsRunner) => {

            let leftNodeText = left.createdDate.toISOString();
            let rightNodeText = right.createdDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }

    sortByFinalizedDate(runners: Array<ManualTestsRunner>) {
        runners.sort((left: ManualTestsRunner, right: ManualTestsRunner) => {

            let leftNodeText = left.finalizedDate.toISOString();
            let rightNodeText = right.finalizedDate.toISOString();

            if (leftNodeText.toUpperCase() < rightNodeText.toUpperCase()) return 1;
            if (leftNodeText.toUpperCase() > rightNodeText.toUpperCase()) return -1;

            return 0;
        });
    }
}
