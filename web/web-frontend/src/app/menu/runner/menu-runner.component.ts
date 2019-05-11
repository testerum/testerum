import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {RunnerService} from "../../service/runner.service";
import {RunnerConfig} from "../../functionalities/config/runner/model/runner-config.model";
import {StringSelectItem} from "../../model/prime-ng/StringSelectItem";
import {RunnerModalService} from "../../functionalities/config/runner/runner-modal.service";
import {RunnerConfigService} from "../../functionalities/config/runner/runner-config.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'menu-runner',
    templateUrl: './menu-runner.component.html',
    styleUrls: ['./menu-runner.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class MenuRunnerComponent implements OnInit, OnDestroy {

    private static EDIT_CONFIG_KEY = "editConfig";

    runners: Array<RunnerConfig> = [];
    runnersSelectItems: StringSelectItem[] = [];
    selectedItem: string;

    private runnerConfigSubscription: Subscription;
    private selectedRunnerSubscription: Subscription;
    constructor(private runnerService: RunnerService,
                private runnerConfigService: RunnerConfigService,
                private runnerModalService: RunnerModalService) {
    }

    ngOnInit() {
        this.runnerConfigSubscription = this.runnerService.getRunnerConfig().subscribe((runners: Array<RunnerConfig>) => {
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
        });

        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe( (selectedRunnerConfigs: Array<RunnerConfig>) => {
            if(selectedRunnerConfigs.length == 0 || selectedRunnerConfigs.length > 1) {
                this.selectedItem = null;
                return;
            }
            this.selectedItem = selectedRunnerConfigs[0].name;
        });
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
            this.runnerModalService.showRunnersModal(this.runners);
        }
    }

    onRunnersDropDownClick() {
        if (this.hasNoRunnerConfig()) {
            this.runnerModalService.showRunnersModal(this.runners);
        }
    }
}
