import {
    AfterViewInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentRef,
    EventEmitter,
    ViewChild
} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {RunnerConfig} from "./model/runner-config.model";

@Component({
    selector: 'runner-modal',
    templateUrl: './runner-modal.component.html',
    styleUrls: ['./runner-modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RunnerModalComponent implements AfterViewInit {

    @ViewChild("runnerModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<RunnerModalComponent>;

    selectedCategory: string;
    runnerConfigSelectedEventEmitter: EventEmitter<RunnerConfig> = new EventEmitter<RunnerConfig>();

    constructor(private cd: ChangeDetectorRef) {
    }

    ngAfterViewInit(): void {
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
        this.runnerConfigSelectedEventEmitter.complete();
        this.modal.hide();
        this.refresh();
    }
}
