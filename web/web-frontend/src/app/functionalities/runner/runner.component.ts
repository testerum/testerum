import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Params, Router} from "@angular/router";
import {ResultService} from "../../service/report/result.service";

@Component({
    selector: 'runner',
    templateUrl: 'runner.component.html',
    styleUrls: ["../../generic/css/main-container.css"]
})

export class RunnerComponent implements OnInit {

    isItemSelected: boolean = true;

    constructor(private router: Router) {
    }

    ngOnInit() {
        this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(route => {
                let router = this.router;
                let leafRoute: any = this.router.routerState.snapshot.root;
                while (leafRoute.firstChild) leafRoute = leafRoute.firstChild;

                return leafRoute.params
            })
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
}
