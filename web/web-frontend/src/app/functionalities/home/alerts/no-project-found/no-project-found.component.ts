import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {InfoModalComponent} from "../../../../generic/components/info_modal/info-modal.component";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {UrlService} from "../../../../service/url.service";

@Component({
    selector: 'no-project-found',
    templateUrl: './no-project-found.component.html',
    styleUrls: ['./no-project-found.component.scss']
})
export class NoProjectFoundComponent implements OnInit, OnDestroy {

    @ViewChild(InfoModalComponent) infoModalComponent: InfoModalComponent;

    private routeSubscription: Subscription;
    constructor(private activatedRoute: ActivatedRoute,
                private router: Router,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.routeSubscription = this.activatedRoute.data.subscribe(data => {
            let projectName = this.activatedRoute.snapshot.params['projectName'];

            this.infoModalComponent.show("Project Not Found",
                "Project \"<b>"+projectName+"</b>\" is not known by this Testerum instance.",
                [
                    "Please first open this project with Testerum and retry your URL."
                ]
            ).subscribe( value => {
                this.urlService.navigateToHomePage();
            });
        })
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) {this.routeSubscription.unsubscribe()}
    }
}
