import {Component, OnDestroy, OnInit} from '@angular/core';
import {Project} from "../../model/home/project.model";
import {HomeService} from "../../service/home.service";
import {Subscription} from "rxjs";
import {UrlService} from "../../service/url.service";
import {ContextService} from "../../service/context.service";
import {Home} from "../../model/home/home.model";
import {ArrayUtil} from "../../utils/array.util";

@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    quote: string;
    testerumVersion: string;
    recentProjects: Project[] = [];

    private getHomePageModelSubscription: Subscription;
    constructor(private homeService: HomeService,
                private contextService: ContextService,
                private urlService: UrlService) {
    }

    ngOnInit() {
        this.getHomePageModelSubscription = this.homeService.getHomePageModel().subscribe((home: Home) => {
            this.quote = home.quote;
            this.testerumVersion = home.testerumVersion;
            ArrayUtil.replaceElementsInArray(this.recentProjects, home.recentProjects);
        });
    }

    ngOnDestroy(): void {
        if (this.getHomePageModelSubscription) this.getHomePageModelSubscription.unsubscribe();
    }

    navigateToProject(recentProject: Project) {
        this.contextService.project = recentProject;
        this.urlService.navigateToFeatures();
    }
}
