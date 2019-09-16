import {ChangeDetectionStrategy, Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../../utils/array.util";
import {FeaturesTreeService} from "../features-tree.service";
import {FeaturesTreeFilter} from "../../../../model/feature/filter/features-tree-filter.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ActivatedRoute} from "@angular/router";
import {JsonTreeExpandUtil} from "../../../../generic/components/json-tree/util/json-tree-expand.util";
import {TagsService} from "../../../../service/tags.service";

@Component({
    selector: 'features-tree-filter',
    templateUrl: 'features-tree-filter.component.html',
    styleUrls: ['features-tree-filter.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class FeaturesTreeFilterComponent implements OnInit {

    @Input() treeModel:JsonTreeModel;

    @ViewChild("tagsElement", { static: false }) tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    selectedTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    searchText: string;

    isAutomatedButtonActive = false;
    isManualButtonActive = false;
    isSearchButtonActive = false;
    isTagsButtonActive = false;

    constructor(private activatedRoute: ActivatedRoute,
                private featureTreeService: FeaturesTreeService,
                private tagsService: TagsService) {
    }

    ngOnInit() {
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
    onAutomatedButtonClickEvent() {
        this.isAutomatedButtonActive = !this.isAutomatedButtonActive;
        this.filter()
    }
    onManualButtonClickEvent() {
        this.isManualButtonActive = !this.isManualButtonActive;
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
        let featuresTreeFilter = new FeaturesTreeFilter();
        featuresTreeFilter.includeAutomatedTests = this.isAutomatedButtonActive || this.isManualButtonActive == this.isAutomatedButtonActive;
        featuresTreeFilter.includeManualTests = this.isManualButtonActive || this.isManualButtonActive == this.isAutomatedButtonActive;

        if (this.isSearchButtonActive) {
            featuresTreeFilter.search = this.searchText;
        }

        if (this.isTagsButtonActive) {
            featuresTreeFilter.tags = this.selectedTags;
        }

        this.featureTreeService.treeFilter = featuresTreeFilter;

        let path = this.getCurrentPathFromUrl();
        this.featureTreeService.initializeTestsTreeFromServer(path, 100);
    }

    private getCurrentPathFromUrl(): Path {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path'] : null;
        return pathAsString != null ? Path.createInstance(pathAsString) : null;
    }

    onExpandSelectedNodeEvent() {
        let nodeToExpand = this.treeModel.selectedNode ? this.treeModel.selectedNode : this.treeModel;
        JsonTreeExpandUtil.expandNode(nodeToExpand);
    }

    onExpandToLevelEvent(level: number) {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, level+1);
    }
}
