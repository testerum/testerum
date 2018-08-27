import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";

@Component({
    selector: 'manual-exec-plans',
    templateUrl: 'manual-exec-plans.component.html'
})
export class ManualExecPlansComponent implements OnInit {

    isTestSelected: boolean = true;

    constructor(private router: Router) {
    }

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
}

