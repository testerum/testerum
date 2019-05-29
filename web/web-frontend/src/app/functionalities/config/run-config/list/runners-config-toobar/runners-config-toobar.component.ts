import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import {RunConfigService} from "../../run-config.service";
import {RunConfig} from "../../model/runner-config.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'runners-config-toobar',
    templateUrl: './runners-config-toobar.component.html',
    styleUrls: ['./runners-config-toobar.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnersConfigToobarComponent implements OnInit, OnDestroy {

    selectedRunnerSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private runnerConfigService: RunConfigService) {
    }

    ngOnInit() {
        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfig: RunConfig|null) =>{
            this.refresh();
        });
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    isRunnersConfigSelected(): boolean {
        return this.runnerConfigService.areRunnersSelected();
    }

    isRunnerConfigSelectedAndNotTheFirstItem(): boolean {
        return this.runnerConfigService.isRunnerConfigSelectedAndNotTheFirstItem();
    }

    isRunnerConfigSelectedAndNotTheLastItem(): boolean {
        return this.runnerConfigService.isRunnerConfigSelectedAndNotTheLastItem();
    }

    isOnlyOneRunnerConfigSelected(): boolean {
        return this.runnerConfigService.selectedRunners.length == 1;
    }

    onAddNewConfiguration() {
        this.runnerConfigService.addNewRunnerConfig();
    }

    onRemoveConfiguration() {
        this.runnerConfigService.removeSelectedRunnerConfig();
    }

    onCopyConfiguration() {
        this.runnerConfigService.copyConfiguration();
    }

    onMoveUp() {
        this.runnerConfigService.moveUp();
    }

    onMoveDown() {
        this.runnerConfigService.moveDown();
    }
}
