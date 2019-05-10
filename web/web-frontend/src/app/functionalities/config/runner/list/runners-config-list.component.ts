import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import {RunnerConfigService} from "../runner-config.service";
import {RunnerConfig} from "../model/runner-config.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'runners-config-list',
    templateUrl: './runners-config-list.component.html',
    styleUrls: ['./runners-config-list.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnersConfigListComponent implements OnInit, OnDestroy {

    runners: Array<RunnerConfig> = [];
    selectedRunners: Array<RunnerConfig> = [];

    selectedRunnerSubscription: Subscription;
    refreshConfigListEventEmitter: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private runnerConfigService: RunnerConfigService) {
    }

    ngOnInit() {
        this.runners = this.runnerConfigService.runners;

        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfigs: Array<RunnerConfig>) =>{
            this.selectedRunners = runnerConfigs;

            this.refresh();
        });

        this.refreshConfigListEventEmitter = this.runnerConfigService.refreshConfigListEventEmitter.subscribe(event => {
            this.refresh();
        });

        this.refresh();
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
        if (this.refreshConfigListEventEmitter) this.refreshConfigListEventEmitter.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onOrderItemsSelected($event: any) {
        let selectedRunners: Array<RunnerConfig> = $event.value;
        this.runnerConfigService.setSelectedRunner(selectedRunners);
        this.refresh();
    }
}
