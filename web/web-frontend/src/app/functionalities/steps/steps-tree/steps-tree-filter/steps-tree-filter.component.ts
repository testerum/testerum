import {ChangeDetectionStrategy, Component, ViewChild, ViewEncapsulation} from '@angular/core';
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../../utils/array.util";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";
import {JsonTreeExpandUtil} from "../../../../generic/components/json-tree/util/json-tree-expand.util";
import {TagsService} from "../../../../service/tags.service";
import {StepsTreeService} from "../steps-tree.service";
import {StepsTreeFilter} from "../../../../model/step/filter/steps-tree-filter.model";

@Component({
    selector: 'steps-tree-filter',
    templateUrl: 'steps-tree-filter.component.html',
    styleUrls: ['steps-tree-filter.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class StepsTreeFilterComponent {

    @ViewChild("tagsElement", { static: false }) tagsAutoComplete: AutoComplete;
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
                private stepsTreeService: StepsTreeService,
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

        this.stepsTreeService.treeFilter = stepsTreeFilter;

        let path = this.getCurrentPathFromUrl();
        this.stepsTreeService.initializeStepsTreeFromServer(path, 100);
    }

    private getCurrentPathFromUrl(): Path {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path'] : null;
        return pathAsString != null ? Path.createInstance(pathAsString) : null;
    }

    onExpandAllEvent() {
        JsonTreeExpandUtil.expandNode(this.stepsTreeService.treeModel);
    }

    onExpandToLevelEvent(level: number) {
        JsonTreeExpandUtil.expandTreeToLevel(this.stepsTreeService.treeModel, level+1);
    }
}
