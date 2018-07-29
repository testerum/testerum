import {Component, ViewChild, ViewEncapsulation} from '@angular/core';
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../../utils/array.util";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";
import {JsonTreeExpandUtil} from "../../json-tree/util/json-tree-expand.util";
import {TagsService} from "../../../../service/tags.service";
import {StepsTreeFilter} from "../../../../model/step/filter/steps-tree-filter.model";
import {StepChooserService} from "../step-chooser.service";

@Component({
    selector: 'step-chooser-tree-filter',
    templateUrl: 'step-chooser-tree-filter.component.html',
    styleUrls: ['step-chooser-tree-filter.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class StepChooserTreeFilterComponent {

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    selectedTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    searchText: string;

    isComposedButtonActive = false;
    isBasicButtonActive = false;
    isSearchButtonActive = false;
    isTagsButtonActive = false;

    constructor(private activatedRoute: ActivatedRoute,
                private stepChooserService: StepChooserService,
                private tagsService: TagsService) {
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
        this.filter();
    }

    onTagUnSelect(event) {
        this.filter();
    }

    searchTags(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.selectedTags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow;

        this.filter();
    }

    onSearchInputDeFocus(event: any) {
        this.filter();
    }

    onSearchInputKey(event: KeyboardEvent) {
        if (event.code == "Enter") {
            this.filter();
        }
    }
    onComposedButtonClickEvent() {
        this.isComposedButtonActive = !this.isComposedButtonActive;
        this.filter()
    }
    onBasicButtonClickEvent() {
        this.isBasicButtonActive = !this.isBasicButtonActive;
        this.filter()
    }
    onSearchButtonClickEvent() {
        this.isSearchButtonActive = !this.isSearchButtonActive;
        if(!this.isSearchButtonActive) this.filter()
    }
    onTagsButtonClickEvent() {
        this.isTagsButtonActive = !this.isTagsButtonActive;
        if (this.isTagsButtonActive) {
            this.tagsService.getTags().subscribe(tags => {
                ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
            });
        } else {
            this.filter();
        }
    }

    filter(): void {
        let stepsTreeFilter = new StepsTreeFilter();
        stepsTreeFilter.showComposedTests = this.isComposedButtonActive || this.isBasicButtonActive == this.isComposedButtonActive;
        stepsTreeFilter.showBasicTest = this.isBasicButtonActive || this.isBasicButtonActive == this.isComposedButtonActive;

        if (this.isSearchButtonActive) {
            stepsTreeFilter.search = this.searchText;
        }

        if (this.isTagsButtonActive) {
            stepsTreeFilter.tags = this.selectedTags;
        }

        this.stepChooserService.treeFilter = stepsTreeFilter;

        let path = this.getCurrentPathFromUrl();
        this.stepChooserService.initializeStepsTreeFromServer(path, 100);
    }

    private getCurrentPathFromUrl(): Path {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path'] : null;
        return pathAsString != null ? Path.createInstance(pathAsString) : null;
    }

    onExpandAllEvent() {
        JsonTreeExpandUtil.expandNode(this.stepChooserService.treeModel);
    }

    onExpandToLevelEvent(level: number) {
        JsonTreeExpandUtil.expandTreeToLevel(this.stepChooserService.treeModel, level+1);
    }
}
