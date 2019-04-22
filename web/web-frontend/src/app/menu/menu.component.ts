import {Component} from "@angular/core";
import {NavigationEnd, Router} from "@angular/router";
import {animate, state, style, transition, trigger} from '@angular/animations';
import {ContextService} from "../service/context.service";
import {UrlService} from "../service/url.service";
import {FeedbackModalService} from "../functionalities/user/feedback/feedback-modal.service";
import {LicenseModalService} from "../functionalities/user/license/modal/license-modal.service";

@Component({
    moduleId: module.id,
    selector: "menu",
    templateUrl: "menu.component.html",
    styleUrls: ["menu.component.scss"],
    animations: [
        trigger('subMenu', [
            state('inactive', style({
                backgroundColor: '#eee',
                transform: 'scale(1)'
            })),
            state('active',   style({
                backgroundColor: '#cfd8dc',
                transform: 'scale(1.1)'
            })),
            transition('inactive => active', animate('100ms ease-in')),
            transition('active => inactive', animate('100ms ease-out'))
        ])
    ]
})
export class MenuComponent {

    shouldDisplay = false;

    constructor(private router:Router,
                private contextService: ContextService,
                public urlService: UrlService,
                private licenseModalService: LicenseModalService,
                private feedbackModalService: FeedbackModalService) {
        router.events.subscribe(event => {

            if (event instanceof NavigationEnd) {
                switch(event.url) {
                    case "/license": {
                        this.shouldDisplay = false;
                        break;
                    }
                    default: {
                        this.shouldDisplay = true;
                    }
                }
           }
        });
    }

    private isUrlStartingWith(urlPrefix: string, navigationEndEvent: NavigationEnd): boolean {
        if(navigationEndEvent.url.startsWith(urlPrefix)) {
            return true;
        }

        if (navigationEndEvent.urlAfterRedirects && navigationEndEvent.urlAfterRedirects.startsWith(urlPrefix)) {
            return true
        }
        return false;
    }

    isProjectSelected(): boolean {
        return this.contextService.isProjectSelected();
    }

    getProjectName(): string {
        return this.contextService.getProjectName();
    }

    showFeedback() {
        this.feedbackModalService.showFeedbackModal();
    }

    showLicense() {
        this.licenseModalService.showLicenseModal();
    }

    isUserAuthenticated(): boolean {
        return this.contextService.license.isLoggedIn();
    }

    logout () {
        this.contextService.license.logout();
        this.urlService.navigateToLicense(window.location.href);
    }
}

