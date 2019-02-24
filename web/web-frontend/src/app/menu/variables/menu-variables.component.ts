import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {VariablesService} from "../../service/variables.service";
import {AllProjectVariables} from "../../functionalities/variables/model/project-variables.model";
import {Subscription} from "rxjs";
import {VariablesComponent} from "../../functionalities/variables/variables.component";
import {ContextService} from "../../service/context.service";
import {Project} from "../../model/home/project.model";

@Component({
    selector: 'menu-variables',
    templateUrl: './menu-variables.component.html',
    styleUrls: ['./menu-variables.component.scss']
})
export class MenuVariablesComponent implements OnInit, OnDestroy {

    @ViewChild(VariablesComponent) variablesComponent: VariablesComponent;

    shouldDisplayEnvironmentChooser: boolean = false;
    selectedEnvironment: string;
    availableEnvironments: string[] = [];

    private getVariablesSubscription: Subscription;
    private projectChangedEventSubscription: Subscription;
    constructor(private variablesService: VariablesService,
                private contextService: ContextService) {
    }

    ngOnInit() {
        this.init();
        this.projectChangedEventSubscription = this.contextService.projectChangedEventEmitter.subscribe( (project: Project) => {
            this.init();
        })
    }

    private init() {
        this.getVariablesSubscription = this.variablesService.getVariables().subscribe((projectVariables: AllProjectVariables) => {
            this.selectedEnvironment = projectVariables.currentEnvironment ? projectVariables.currentEnvironment : AllProjectVariables.DEFAULT_ENVIRONMENT_NAME;

            this.availableEnvironments.length = 0;
            this.availableEnvironments.push(AllProjectVariables.DEFAULT_ENVIRONMENT_NAME);
            if (projectVariables.localVariables.length > 0) {
                this.availableEnvironments.push(AllProjectVariables.LOCAL_ENVIRONMENT_NAME);
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
        if(this.projectChangedEventSubscription) this.projectChangedEventSubscription.unsubscribe();
    }

    showVariables() {
        this.variablesComponent.show(this.selectedEnvironment).subscribe(it => {
            this.init();
        });
    }

    onEnvironmentChange(event: any) {
        this.variablesService.saveCurrentEnvironment(this.selectedEnvironment).subscribe(() => {});
    }
}
