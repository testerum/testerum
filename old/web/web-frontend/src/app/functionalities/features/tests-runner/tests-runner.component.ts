import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {TestsRunnerService} from "./tests-runner.service";
import {Subscription} from "rxjs";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    moduleId: module.id,
    selector: 'tests-runner',
    templateUrl: 'tests-runner.component.html'
})
export class TestsRunnerComponent implements OnInit, OnDestroy {

    private runnerChangeSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private testsRunnerService: TestsRunnerService) { }

    ngOnInit(): void {
        this.runnerChangeSubscription = this.testsRunnerService.runnerVisibleEventEmitter.subscribe(() => {
            this.refresh();
        });
    }

    ngOnDestroy(): void {
        if(this.runnerChangeSubscription) this.runnerChangeSubscription.unsubscribe();
    }

    isTestRunnerVisible(): boolean {
        return this.testsRunnerService.isRunnerVisible();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
