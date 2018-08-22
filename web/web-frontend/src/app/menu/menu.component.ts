import {Component, ViewChild} from "@angular/core";
import {VariablesComponent} from "../functionalities/variables/variables.component";
import {NavigationEnd, Router} from "@angular/router";
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
    moduleId: module.id,
    selector: "menu",
    templateUrl: "menu.component.html",
    styleUrls: ["menu.component.scss"],
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

