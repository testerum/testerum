import {filter, map} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {FeaturesTreeService} from "./features-tree/features-tree.service";
import {TestsRunnerService} from "./tests-runner/tests-runner.service";

@Component({
    moduleId: module.id,
    selector: 'features',
    templateUrl: 'features.component.html',
    styleUrls: ["features.component.scss"]
})
export class FeaturesComponent implements OnInit {

    isTestSelected: boolean = true;


    constructor(public featuresTreeService: FeaturesTreeService,
                private testsRunnerService: TestsRunnerService,
                private router: Router) { }

    ngOnInit() {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            }),)
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

    isTestRunnerVisible(): boolean {
        return this.testsRunnerService.isTestRunnerVisible;
    }
}
