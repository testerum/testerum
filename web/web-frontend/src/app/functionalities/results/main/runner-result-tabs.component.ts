import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnDestroy,
    OnInit,
    ViewEncapsulation
} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Params, Router} from "@angular/router";
import {Subscription} from "rxjs";
import {filter, map} from "rxjs/operators";
import {ResultService} from "../../../service/report/result.service";

@Component({
    selector: 'runner-result-tabs',
    templateUrl: 'runner-result-tabs.component.html',
    styleUrls: ["runner-result-tabs.component.scss"],
    encapsulation: ViewEncapsulation.None,
    changeDetection:ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class RunnerResultTabsComponent implements OnInit, OnDestroy {
    tagsResultsUrlSuffix = "#/tags?hideLinksToOtherReports=true";
    testsResultsUrlSuffix = "#/?hideLinksToOtherReports=true";

    testsResultUrl;
    tagsResultsUrl;
    statisticsUrl;

    private activeTabIndex: number = 0;

    routerEventsSubscription: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private router: Router,
                private activatedRoute: ActivatedRoute,
                private resultService: ResultService) {
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
                    this.testsResultUrl = null;
                    this.tagsResultsUrl = null;

                    this.setReportBaseUrl(params["url"]);
                }
            );
        this.setReportBaseUrl(this.activatedRoute.snapshot.params["url"]);

        this.resultService.getStatisticsUrl().subscribe((statisticsUrl: string) => {
            this.statisticsUrl = statisticsUrl;
            this.refresh()
        });
        this.refresh()
    }

    ngOnDestroy(): void {
        if (this.routerEventsSubscription) this.routerEventsSubscription.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }

    private setReportBaseUrl(baseUrl: string) {
        this.testsResultUrl = baseUrl ? baseUrl + this.testsResultsUrlSuffix : null;
        this.tagsResultsUrl = baseUrl ? baseUrl + this.tagsResultsUrlSuffix : null;

        this.activeTabIndex = baseUrl ? 0 : 2;

        this.refresh();
    }
}
