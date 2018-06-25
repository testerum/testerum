import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ExecutionPieModel} from "./model/execution-pie.model";
import {ExecutionPieService} from "./execution-pie.service";

@Component({
    selector: 'execution-pie',
    templateUrl: 'execution-pie.component.html',
    styleUrls: ['execution-pie.component.css']
})

export class ExecutionPieComponent implements OnInit {

    @Input() model: ExecutionPieModel = new ExecutionPieModel();
    @Input() width: string;
    @Input() height: string;
    @Input() showLegend: boolean = true;

    pieChartData: any;

    pieChartOptions:any;

    constructor(private executionPieService: ExecutionPieService) {
    }

    ngOnInit(): void {
        this.executionPieService.setExecutionPieModel(this.model);

        this.pieChartOptions = {
            animation: false,
            legend: {
                display: false,
            }
        };

        this.pieChartData = this.getPieDataFromModel(this.model);
        this.model.changeEventEmitter.subscribe(
            event => {
                this.pieChartData = this.getPieDataFromModel(this.model);
            }
        );
    }

    getPieDataFromModel(model: ExecutionPieModel): any {
        return {
            labels: [
                'Waiting to execute',
                'Passed',
                'Failed',
                'Errors',
                'Undefined steps',
                'Skipped'
                ],
            datasets: [{
                data: [
                    model.waitingToExecute,
                    model.passed,
                    model.failed,
                    model.error,
                    model.undefined,
                    model.skipped
                ],
                backgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#ff0000", "#FFCE56", "#aae8ff"],
                hoverBackgroundColor: ["#c0bebc", "#5cb85c", "#FF6384", "#ff0000", "#FFCE56", "#aae8ff"]
            }]
        };
    }
}
