import {Component, OnDestroy, OnInit} from '@angular/core';
import {VariablesService} from "../../service/variables.service";
import {ProjectVariables} from "../../functionalities/variables/model/project-variables.model";
import {Subscription} from "rxjs";

@Component({
    selector: 'menu-variables',
    templateUrl: './menu-variables.component.html',
    styleUrls: ['./menu-variables.component.scss']
})
export class MenuVariablesComponent implements OnInit, OnDestroy {

    shouldDisplayEnvironmentChooser: boolean = false;
    selectedEnvironment: string;
    availableEnvironments: string[] = [];

    private getVariablesSubscription: Subscription;
    constructor(private variablesService: VariablesService) {
    }

    ngOnInit() {
        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: ProjectVariables) => {
            this.selectedEnvironment = projectVariables.currentEnvironment ? projectVariables.currentEnvironment : VariablesService.DEFAULT_ENVIRONMENT_NAME;
            this.availableEnvironments.push(VariablesService.DEFAULT_ENVIRONMENT_NAME);
            if (projectVariables.localVariables.length > 0) {
                this.availableEnvironments.push(VariablesService.LOCAL_ENVIRONMENT_NAME);
            }

            for (const environment of projectVariables.environments) {
                this.availableEnvironments.push(environment.name);
            }

            if (this.availableEnvironments.length > 1) {
                this.shouldDisplayEnvironmentChooser = true
            }
        });
    }

    ngOnDestroy(): void {
        if(this.getVariablesSubscription) this.getVariablesSubscription.unsubscribe();
    }
}
