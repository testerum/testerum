import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    OnDestroy,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {RunConfig} from "./model/runner-config.model";
import {Subscription} from "rxjs";
import {RunConfigService} from "./run-config.service";
import {RunnerService} from "../../../service/runner.service";

@Component({
    selector: 'run-config-modal',
    templateUrl: './run-config-modal.component.html',
    styleUrls: ['./run-config-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunConfigModalComponent implements AfterViewInit, OnDestroy {

    @ViewChild("runnerModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<RunConfigModalComponent>;

    selectedCategory: string;

    private selectedRunnerSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private runnerService: RunnerService,
                private runnerConfigService: RunConfigService) {
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    ngAfterViewInit(): void {
        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runConfigs: Array<RunConfig>) =>{
            this.refresh();
        });

        this.modal.show();
        this.modal.onHidden.subscribe(event => {
            this.modalComponentRef.destroy();
            this.modalComponentRef = null;
        });
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    onCategorySelected(selectedCategory) {
        this.selectedCategory = selectedCategory;
        this.refresh();
    }

    cancel() {
        this.modal.hide();
        this.refresh();
    }

    saveAction() {
        this.runnerService.saveRunnerConfig(this.runnerConfigService.runners).subscribe((runnerConfigs: Array<RunConfig>)=> {

        });
    }
}
