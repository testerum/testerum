import {AfterViewInit, ChangeDetectorRef, Component, ComponentRef, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {RunnerConfig} from "./model/runner-config.model";

@Component({
  selector: 'runner-modal',
  templateUrl: './runner-modal.component.html',
  styleUrls: ['./runner-modal.component.scss']
})
export class RunnerModalComponent implements AfterViewInit {

    @ViewChild("runnerModal") modal: ModalDirective;
    modalComponentRef: ComponentRef<RunnerModalComponent>;

    selectedCategory: string;

    constructor(private cd: ChangeDetectorRef) {}

    public init(runners: Array<RunnerConfig>) {

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
        this.modal.hide();
    }
}
