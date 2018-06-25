import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";

@Component({
    selector: 'manual-tests-runner',
    templateUrl: 'manual-tests-runner.component.html',
    styleUrls: ['manual-tests-runner.component.css', '../../generic/css/forms.css', "../../generic/css/main-container.css"]
})

export class ManualTestsRunnerComponent implements OnInit {

    isTestSelected: boolean = true;

    constructor(private router: Router) {
    }

    ngOnInit() {

        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
            .subscribe((params: Params) => {
                    let action = params['action'];
                    if (action) {
                        this.isTestSelected = true;
                    } else {
                        this.isTestSelected = false;
                    }
                }
            );
    }
}
