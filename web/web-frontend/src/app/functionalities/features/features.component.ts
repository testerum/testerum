import {filter, map} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {FeaturesTreeService} from "./features-tree/features-tree.service";

@Component({
    moduleId: module.id,
    selector: 'features',
    templateUrl: 'features.component.html',
    styleUrls: ["features.component.scss"]
})
export class FeaturesComponent implements OnInit {

    isTestSelected: boolean = true;

    featuresTreeService: FeaturesTreeService;

    constructor(featuresTreeService: FeaturesTreeService,
                private router: Router) {
        this.featuresTreeService = featuresTreeService;
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
