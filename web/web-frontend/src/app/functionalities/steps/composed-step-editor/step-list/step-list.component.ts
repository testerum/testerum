
import {Component, OnInit, Input, OnChanges, SimpleChanges, EventEmitter, Output} from '@angular/core';
import {TestModel} from "../../../../model/test/test.model";
import {StepListService} from "./step-list.service";

@Component({
    moduleId: module.id,
    selector: 'step-list',
    templateUrl: 'step-list.component.html',
    styleUrls:['step-list.component.css']
})
export class StepListComponent implements OnChanges {

    @Input() testModel:TestModel;
    @Input() isEditMode: boolean;

    constructor(private stepListService: StepListService) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.stepListService.testModel = this.testModel;
    }

    onDropSuccess($event: any) {
        this.stepListService.triggerStepOrderChangedEvent();
    }
}
