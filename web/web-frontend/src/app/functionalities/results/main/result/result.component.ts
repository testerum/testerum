import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {filter, map} from "rxjs/operators";
import {UrlUtil} from "../../../../utils/url.util";
import {Subscription} from "rxjs";

@Component({
    selector: 'result',
    templateUrl: 'result.component.html',
    styleUrls: ['result.component.scss']
})
export class ResultComponent implements OnInit, OnDestroy {

    @Input() urlSuffix: string;

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
                    this.refreshUrl(params["url"])
                }
            );
        this.refreshUrl(this.activatedRoute.snapshot.params["url"])
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }

    private refreshUrl(baseUrl: string) {
        this.url = baseUrl + (this.urlSuffix ? this.urlSuffix: "")
    }
}
