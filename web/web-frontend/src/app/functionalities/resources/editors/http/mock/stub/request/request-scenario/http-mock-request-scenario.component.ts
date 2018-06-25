import {Component, OnInit} from '@angular/core';
import {HttpMockService} from "../../http-mock.service";
import {HttpMockRequestScenario} from "../../model/request/http-mock-request-scenario.model";
import {HttpMockRequestScenarioService} from "./http-mock-request-scenario.service";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request-scenario',
    templateUrl: 'http-mock-request-scenario.component.html',
    styleUrls: ["http-mock-request-scenario.component.css"]
})
export class HttpMockRequestScenarioComponent implements OnInit {

    suggestionScenarioNames: any[];
    suggestionScenarioStates: any[];

    constructor(private httpMockService: HttpMockService,
                private httpMockRequestScenarioService: HttpMockRequestScenarioService) {
    }

    ngOnInit(): void {
        this.suggestionScenarioNames = this.httpMockRequestScenarioService.getAllKnownScenarioNames();
        this.suggestionScenarioStates = this.httpMockRequestScenarioService.getAllKnownScenarioStates();
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode
    }

    isNotEmpty() {
        return this.getModel().scenarioName || this.getModel().currentState || this.getModel().newState;
    }

    getModel(): HttpMockRequestScenario {
        return this.httpMockService.httpMock.expectedRequest.scenario;
    }

    getKnownScenarioNames(): Array<string> {
        return this.httpMockRequestScenarioService.getAllKnownScenarioNames();
    }

    filterSuggestionScenarioNames(event: any) {
        this.suggestionScenarioNames = [];
        for(let i = 0; i <this.getKnownScenarioNames().length; i++) {
            let scenarioName = this.getKnownScenarioNames()[i];
            if(scenarioName.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
                this.suggestionScenarioNames.push(scenarioName);
            }
        }
    }

    getAllKnownScenarioStates(): Array<string> {
        return this.httpMockRequestScenarioService.getAllKnownScenarioStates();
    }

    filterSuggestionScenarioStates(event: any) {
        this.suggestionScenarioStates = [];
        for(let i = 0; i <this.getAllKnownScenarioStates().length; i++) {
            let scenarioStates = this.getAllKnownScenarioStates()[i];
            if(scenarioStates.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
                this.suggestionScenarioStates.push(scenarioStates);
            }
        }
    }

    deleteScenario() {
        this.getModel().scenarioName = null;
        this.getModel().currentState = null;
        this.getModel().newState = null;
    }
}
