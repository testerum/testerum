import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {RunConfigService} from "../../service/run-config.service";
import {RunConfig} from "../../functionalities/config/run-config/model/runner-config.model";
import {StringSelectItem} from "../../model/prime-ng/StringSelectItem";
import {RunConfigModalService} from "../../functionalities/config/run-config/run-config-modal.service";
import {RunConfigComponentService} from "../../functionalities/config/run-config/run-config-component.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'menu-runner',
    templateUrl: './menu-runner.component.html',
    styleUrls: ['./menu-runner.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class MenuRunnerComponent implements OnInit, OnDestroy {

    private static EDIT_CONFIG_KEY = "editConfig";

    runners: Array<RunConfig> = [];
    runnersSelectItems: StringSelectItem[] = [];
    selectedItem: string;

    private runnerConfigSubscription: Subscription;
    private selectedRunnerSubscription: Subscription;
    constructor(private runConfigService: RunConfigService,
                private runConfigComponentService: RunConfigComponentService,
                private runConfigModalService: RunConfigModalService) {
    }

    ngOnInit() {
        this.runnerConfigSubscription = this.runConfigService.getRunnerConfig().subscribe((runners: Array<RunConfig>) => {
            this.initRunners(runners);
        });

        this.selectedRunnerSubscription = this.runConfigComponentService.selectedRunnerEventEmitter.subscribe( (selectedRunnerConfigs: Array<RunConfig>) => {
            if(selectedRunnerConfigs.length == 0 || selectedRunnerConfigs.length > 1) {
                this.selectedItem = null;
                return;
            }
            this.selectedItem = selectedRunnerConfigs[0].name;
        });
    }

    private initRunners(runners: Array<RunConfig>) {
        this.runners = runners;

        let runnersSelectItems = [];
        if (runners.length > 0) {
            runnersSelectItems.push(
                new StringSelectItem("Edit Configurations...", MenuRunnerComponent.EDIT_CONFIG_KEY, false, "fas fa-pencil-alt")
            );
        }

        for (const runner of runners) {
            runnersSelectItems.push(
                new StringSelectItem(
                    runner.name,
                    runner.name,
                    false,
                    "fas fa-bolt"
                )
            )
        }
        this.runnersSelectItems = runnersSelectItems;
    }

    ngOnDestroy(): void {
        if (this.runnerConfigSubscription) this.runnerConfigSubscription.unsubscribe();
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    hasNoRunnerConfig(): boolean {
        return this.runnersSelectItems.length == 0;
    }

    isRunnableConfigSelected(): boolean {
        if(this.selectedItem && this.selectedItem != MenuRunnerComponent.EDIT_CONFIG_KEY) {
            return true;
        }
        return false;
    }

    onRunnerItemSelection() {
        if (this.selectedItem == MenuRunnerComponent.EDIT_CONFIG_KEY) {
            this.runConfigModalService.showRunnersModal(this.runners).subscribe( (savedRunner: RunConfig[]) => {
                this.initRunners(savedRunner);
            });
        }
    }

    onRunnersDropDownClick() {
        if (this.hasNoRunnerConfig()) {
            this.runConfigModalService.showRunnersModal(this.runners).subscribe( (savedRunner: RunConfig[]) => {
                this.initRunners(savedRunner);
            });
        }
    }
}
