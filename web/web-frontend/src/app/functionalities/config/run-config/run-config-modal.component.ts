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
import {RunConfigComponentService} from "./run-config-component.service";
import {RunConfigService} from "../../../service/run-config.service";

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
                private runConfigService: RunConfigService,
                private runConfigComponentService: RunConfigComponentService) {
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    ngAfterViewInit(): void {
        this.selectedRunnerSubscription = this.runConfigComponentService.selectedRunnerEventEmitter.subscribe((runConfigs: Array<RunConfig>) =>{
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
        this.runConfigComponentService.savedRunConfigsEventEmitter.complete();
        this.modal.hide();
        this.refresh();
    }

    saveAction() {
        this.runConfigService.saveRunnerConfig(this.runConfigComponentService.runners).subscribe((runConfigs: Array<RunConfig>)=> {
            this.runConfigComponentService.savedRunConfigsEventEmitter.emit(runConfigs);
            this.cancel();
        });
    }
}
