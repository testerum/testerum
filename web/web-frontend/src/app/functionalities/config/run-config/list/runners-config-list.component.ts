import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import {RunConfigService} from "../run-config.service";
import {RunConfig} from "../model/runner-config.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'runners-config-list',
    templateUrl: './runners-config-list.component.html',
    styleUrls: ['./runners-config-list.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnersConfigListComponent implements OnInit, OnDestroy {

    runners: Array<RunConfig> = [];
    selectedRunners: Array<RunConfig> = [];

    selectedRunnerSubscription: Subscription;
    refreshConfigListEventEmitter: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private runnerConfigService: RunConfigService) {
    }

    ngOnInit() {
        this.runners = this.runnerConfigService.runners;

        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfigs: Array<RunConfig>) =>{
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
        let selectedRunners: Array<RunConfig> = $event.value;
        this.runnerConfigService.setSelectedRunner(selectedRunners);
        this.refresh();
    }
}
