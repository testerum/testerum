import {Component, Input, OnInit} from '@angular/core';
import {ExecutionPieModel} from "./model/execution-pie.model";
import {ExecutionPieService} from "./execution-pie.service";

@Component({
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

    constructor(private executionPieService: ExecutionPieService) { }

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
            }
        );
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
                backgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#ff0000", "#FFCE56", "#aae8ff"],
                hoverBackgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#ff0000", "#FFCE56", "#aae8ff"]
            }]
        };
    }

    model(): ExecutionPieModel {
        return this.executionPieService.pieModel;
    }
}
