import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";
import {UrlUtil} from "../../../../utils/url.util";
import {Subscription} from "rxjs";

@Component({
    selector: 'result',
    templateUrl: 'result.component.html'
})
export class ResultComponent implements OnInit, OnDestroy {

    url: string;

    routerEventsSubscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,) {
    }

    ngOnInit() {

        this.routerEventsSubscription = this.router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            map(route => {
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;
                return leafRoute.params
            }))
            .subscribe((params: Params) => {
                    this.url = params["url"];
                }
            );
        this.url = this.activatedRoute.snapshot.params["url"];
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }
}
