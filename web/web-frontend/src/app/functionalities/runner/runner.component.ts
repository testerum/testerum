import {filter, map} from 'rxjs/operators';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
    selector: 'runner',
    templateUrl: 'runner.component.html'
})

export class RunnerComponent implements OnInit, OnDestroy {

    isItemSelected: boolean = true;
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
                    let action = params['path'];
                    if (action) {
                        this.isItemSelected = true;
                    } else {
                        this.isItemSelected = false;
                    }
                }
            );
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
