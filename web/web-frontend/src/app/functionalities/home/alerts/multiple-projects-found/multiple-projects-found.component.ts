import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Project} from "../../../../model/home/project.model";
import {Subscription} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {MultipleProjectsFound} from "./model/multiple-projects-found.model";
import {ContextService} from "../../../../service/context.service";
import {SelectProjectModalComponent} from "./select-project-modal/select-project-modal.component";

@Component({
    selector: 'multiple-projects-found',
    templateUrl: "multiple-projects-found.component.html",
    styleUrls: ["multiple-projects-found.component.scss"]
})
export class MultipleProjectsFoundComponent implements OnInit, OnDestroy {

    @ViewChild(SelectProjectModalComponent) selectProjectModalComponent: SelectProjectModalComponent;

    private routeSubscription: Subscription;
    constructor(private activatedRoute: ActivatedRoute,
                private router: Router,
                private contextService: ContextService) {
    }

    ngOnInit(): void {
        this.routeSubscription = this.activatedRoute.data.subscribe(data => {
            let modelAsString = this.activatedRoute.snapshot.params['data'];
            let model: MultipleProjectsFound = new MultipleProjectsFound().deserialize(JSON.parse(modelAsString));

            this.selectProjectModalComponent.show(model.projects).subscribe( (selectedProject: Project) => {
                this.contextService.currentProject = selectedProject;
                this.router.navigateByUrl(model.urlToNavigate);
            })
        })
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) {this.routeSubscription.unsubscribe()}
    }
}
