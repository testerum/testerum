import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {RunnerConfigService} from "../runner-config.service";
import {Subscription} from "rxjs";
import {RunnerConfig} from "../model/runner-config.model";
import {FormUtil} from "../../../../utils/form.util";
import {NgForm} from "@angular/forms";

@Component({
    selector: 'runner-config-editor',
    templateUrl: './runner-config-editor.component.html',
    styleUrls: ['./runner-config-editor.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnerConfigEditorComponent implements OnInit, OnDestroy {

    @ViewChild(NgForm) form: NgForm;

    runnerConfig: RunnerConfig|null;

    activeTabIndex: number = 0;

    private selectedRunnerSubscription: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private runnerConfigService: RunnerConfigService) {
    }

    ngOnInit() {
        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfigs: Array<RunnerConfig>) =>{
            if(runnerConfigs.length == 0 || runnerConfigs.length > 1) {
                this.runnerConfig = null;
            } else {
                this.runnerConfig = runnerConfigs[0]
            }

            this.refresh();
        });

        this.refresh();
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    hasRunnerConfigs(): boolean {
        return this.runnerConfigService.runners.length > 0
    }

    onNameChange(event: any): void {
        let runnerConfigByName = this.runnerConfigService.getRunnerConfigByName(this.runnerConfig.name);
        if (runnerConfigByName != this.runnerConfig) {
            FormUtil.addErrorToForm(this.form, "name", "a_resource_with_the_same_name_already_exist");
        }
        this.refresh();
        this.runnerConfigService.refreshConfigListEventEmitter.emit();
    }
}
