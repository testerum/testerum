import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalDirective} from "ngx-bootstrap";
import {HttpMockRequestHeader} from "../resources/editors/http/mock/stub/model/request/http-mock-request-header.model";
import {Variable} from "../../model/variable/variable.model";
import {StringUtils} from "../../utils/string-utils.util";
import {ArrayUtil} from "../../utils/array.util";
import {VariablesService} from "../../service/variables.service";

@Component({
    moduleId: module.id,
    selector: 'variables',
    templateUrl: 'variables.component.html',
    styleUrls: ['variables.component.css']
})

export class VariablesComponent implements OnInit {

    @ViewChild("infoModal") infoModal:ModalDirective;

    variables: Array<Variable> = [];
    editMode: boolean = false;

    constructor(private variablesService: VariablesService) {
    }

    ngOnInit() {
        this.loadVariablesFromServer();
    }

    private loadVariablesFromServer() {
        this.variablesService.getVariables().subscribe(
            (serverVariables: Array<Variable>) => {
                this.variables.length = 0;

                for (let serverVariable of serverVariables) {
                    this.variables.push(serverVariable)
                }
            }
        );
    }

    onNewKeyChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    onNewValueChange(value: Event) {
        this.checkNewKeyValueComplitness();
    }

    private checkNewKeyValueComplitness() {
        let lastKeyValue = this.variables[this.variables.length - 1];
        let lastKey = lastKeyValue.key;
        let lastValue = lastKeyValue.value;

        if (StringUtils.isNotEmpty(lastKey) || StringUtils.isNotEmpty(lastValue)) {
            this.variables.push(
                new Variable()
            );
        }
    }

    enableEditMode() {
        this.editMode = true;
    }

    deleteVariable(variable: Variable) {
        ArrayUtil.removeElementFromArray(this.variables, variable);
    }

    isEditMode(): boolean {
        return this.editMode
    }

    show(): void {
        this.infoModal.show();
    }

    close(): void {
        this.infoModal.hide();
    }

    save(): void {
        let variablesToSave = this.variables.filter(variable => !variable.isEmpty());
        this.variablesService.save(variablesToSave).subscribe(
            result => {
                this.editMode = false;
            }
        )
    }

    cancel(): void {
        this.editMode = false;
        this.loadVariablesFromServer();
    }
}
