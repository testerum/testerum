import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";
import {Subscription} from "rxjs";

@Component({
    selector: 'manual-test-plans',
    templateUrl: 'manual-test-plans.component.html'
})
export class ManualTestPlansComponent implements OnInit, OnDestroy {

    isTestSelected: boolean = true;

    routerEventsSubscription: Subscription;

    constructor(private router: Router) {
    }

    ngOnInit() {
        this.routerEventsSubscription = this.router.events.pipe(
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

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}

