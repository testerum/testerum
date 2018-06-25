import {Component, ViewChild} from "@angular/core";
import {VariablesComponent} from "../functionalities/variables/variables.component";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {trigger, state, style, animate, transition} from '@angular/animations';

@Component({
    moduleId: module.id,
    selector: "menu",
    templateUrl: "menu.component.html",
    styleUrls: ["menu.component.css"],
    animations: [
        trigger('subMenu', [
            state('inactive', style({
                backgroundColor: '#eee',
                transform: 'scale(1)'
            })),
            state('active',   style({
                backgroundColor: '#cfd8dc',
                transform: 'scale(1.1)'
            })),
            transition('inactive => active', animate('100ms ease-in')),
            transition('active => inactive', animate('100ms ease-out'))
        ])
    ]
})
export class MenuComponent {

    @ViewChild(VariablesComponent) variablesComponent: VariablesComponent;

    shouldDisplay = false;

    shouldDisplayAutomatedItems: boolean = false;
    shouldDisplayManualItems: boolean = false;

    isManualRunnerItemActive: boolean = false;

    constructor(private router:Router) {
        router.events.subscribe(event => {

            if (event instanceof NavigationEnd) {
                switch(event.url) {
                    case "/setup": {
                        this.shouldDisplay = false;
                        break;
                    }
                    default: {
                        this.shouldDisplay = true;
                    }
                }

                this.shouldDisplayAutomatedItems = false;
                this.shouldDisplayManualItems = false;
                this.isManualRunnerItemActive = false;

                if (this.isUrlStartingWith("/features", event) || this.isUrlStartingWith("/automated", event)) {
                    this.shouldDisplayAutomatedItems = true;
                }
                if (this.isUrlStartingWith("/manual", event)) {
                    this.shouldDisplayManualItems = true;
                }
                if (this.isUrlStartingWith("/manual/runner", event)
                    || this.isUrlStartingWith("/manual/execute", event)) {
                    this.isManualRunnerItemActive = true;
                }
            }
        });
    }

    private isUrlStartingWith(urlPrefix: string, navigationEndEvent: NavigationEnd): boolean {
        if(navigationEndEvent.url.startsWith(urlPrefix)) {
            return true;
        }

        if (navigationEndEvent.urlAfterRedirects && navigationEndEvent.urlAfterRedirects.startsWith(urlPrefix)) {
            return true
        }
        return false;
    }

    showVariables() {
        this.variablesComponent.show()
    }
}

