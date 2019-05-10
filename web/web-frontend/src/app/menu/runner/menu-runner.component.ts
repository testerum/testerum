import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {RunnerService} from "../../service/runner.service";
import {RunnerConfig} from "../../functionalities/config/runner/model/runner-config.model";
import {StringSelectItem} from "../../model/prime-ng/StringSelectItem";
import {RunnerModalService} from "../../functionalities/config/runner/runner-modal.service";

@Component({
    selector: 'menu-runner',
    templateUrl: './menu-runner.component.html',
    styleUrls: ['./menu-runner.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class MenuRunnerComponent implements OnInit {

    private static EDIT_CONFIG_KEY = "editConfig";

    runners: Array<RunnerConfig> = [];
    runnersSelectItems: StringSelectItem[] = [];
    selectedItem: string;

    constructor(private runnerService: RunnerService,
                private runnerModalService: RunnerModalService) {
    }

    ngOnInit() {
        this.runnerService.getRunnerConfig().subscribe((runners: Array<RunnerConfig>) => {
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
