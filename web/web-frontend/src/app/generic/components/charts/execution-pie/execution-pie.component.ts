import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {ExecutionPieModel} from "./model/execution-pie.model";
import {ExecutionPieService} from "./execution-pie.service";

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
    selector: 'execution-pie',
    templateUrl: 'execution-pie.component.html',
    styleUrls: ['execution-pie.component.scss']
})
export class ExecutionPieComponent implements OnInit {

    @Input() width: string;
    @Input() height: string;
    @Input() showLegend: boolean = true;

    pieChartData: any;

    pieChartOptions:any;

    constructor(private cd: ChangeDetectorRef,
                private executionPieService: ExecutionPieService) { }

    ngOnInit(): void {
        this.pieChartOptions = {
            animation: false,
            legend: {
                display: false,
            }
        };

        this.pieChartData = ExecutionPieComponent.getPieDataFromModel(this.executionPieService.pieModel);
        this.executionPieService.pieModel.changeEventEmitter.subscribe(
            event => {
                this.pieChartData = ExecutionPieComponent.getPieDataFromModel(this.executionPieService.pieModel);
                this.refresh();
            }
        );
        this.refresh();

    }

    private static getPieDataFromModel(model: ExecutionPieModel): any {
        return {
            labels: [
                'Waiting to execute',
                'Passed',
                'Failed',
                'Undefined steps',
                'Skipped'
                ],
            datasets: [{
                data: [
                    model.waitingToExecute,
                    model.passed,
                    model.failed,
                    model.undefined,
                    model.skipped
                ],
                backgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#FFCE56", "#aae8ff"],
                hoverBackgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#FFCE56", "#aae8ff"]
            }]
        };
    }

    model(): ExecutionPieModel {
        return this.executionPieService.pieModel;
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
