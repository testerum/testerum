import {Component, Input, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {ResultFile} from "../../model/result-file.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {StringUtils} from "../../../../../../utils/string-utils.util";

@Component({
    selector: 'result-file',
    templateUrl: 'result-file.component.html',
    styleUrls:['result-file.component.css']
})

export class ResultFileComponent implements OnInit {

    @Input() model:ResultFile;
    private isSelected:boolean = false;

    ExecutionStatusEnum = ExecutionStatusEnum;

    constructor(private router: Router) {
    }

    ngOnInit(): void {

        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                let selectedPath = params['path'];

                if(selectedPath == this.model.path.toString()){
                    this.isSelected = true;
                } else {
                    this.isSelected = false;
                }
            });
    }

    getNodeText(): string {
        let result = StringUtils.substringBefore(this.model.name, "_");
        return result.split("-").join(":");
    }

    showResultFile() {
        this.router.navigate(["/automated/runner/result", {path : this.model.path.toString()} ]);
    }

    getStatusTooltip(): string {
        switch (this.model.executionResult) {
            case ExecutionStatusEnum.WAITING: return "Waiting";
            case ExecutionStatusEnum.EXECUTING : return "Executing";
            case ExecutionStatusEnum.PASSED: return "Passed";
            case ExecutionStatusEnum.FAILED: return "Failed";
            case ExecutionStatusEnum.ERROR: return "Error";
            case ExecutionStatusEnum.UNDEFINED: return "Undefined steps";
            case ExecutionStatusEnum.SKIPPED: return "Skipped";
        }
    }
}
