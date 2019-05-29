import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    EventEmitter, OnDestroy,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {RunnerConfig} from "./model/runner-config.model";
import {Subscription} from "rxjs";
import {RunnerConfigService} from "./runner-config.service";
import {RunnerService} from "../../../service/runner.service";

@Component({
    selector: 'runner-modal',
    templateUrl: './runner-modal.component.html',
    styleUrls: ['./runner-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnerModalComponent implements AfterViewInit, OnDestroy {

    @ViewChild("runnerModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<RunnerModalComponent>;

    selectedCategory: string;

    private selectedRunnerSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                private runnerService: RunnerService,
                private runnerConfigService: RunnerConfigService) {
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerSubscription) this.selectedRunnerSubscription.unsubscribe();
    }

    ngAfterViewInit(): void {
        this.selectedRunnerSubscription = this.runnerConfigService.selectedRunnerEventEmitter.subscribe((runnerConfigs: Array<RunnerConfig>) =>{
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
        this.runnerService.saveRunnerConfig(this.runnerConfigService.runners).subscribe((runnerConfigs: Array<RunnerConfig>)=> {

        });
    }
}
