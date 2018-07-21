import {Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../../utils/array.util";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {FeaturesTreeService} from "../features-tree.service";
import {FeaturesTreeFilter} from "../../../../model/feature/filter/features-tree-filter.model";

@Component({
    selector: 'features-tree-filter',
    templateUrl: 'features-tree-filter.component.html',
    styleUrls: ['features-tree-filter.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class FeaturesTreeFilterComponent implements OnInit {

    @Input() treeModel:JsonTreeModel;

    testTypesFilters: any[];
    selectedTestTypesFilters: any[] = [];

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allTheTags: Array<string> = [];
    selectedTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    searchText: string;

    private automatedButtonValue = "automated";
    private manualButtonValue = "manual";
    private searchButtonValue = "search";
    private tagsButtonValue = "tags";

    isAutomatedButtonActive = false;
    isManualButtonActive = false;
    isSearchButtonActive = false;
    isTagsButtonActive = false;

    constructor(private featureTreeService: FeaturesTreeService) {
        this.testTypesFilters = [
            {title: 'Automated Tests', value: this.automatedButtonValue, icon: 'fa-cog'},
            {title: 'Manual Tests', value: this.manualButtonValue, icon: 'fa-hand-paper-o'},
            {title: 'Search text', value: this.searchButtonValue, icon: 'fa-search'},
            {title: 'Filter by tags', value: this.tagsButtonValue, icon: 'fa-tag'},
        ];
    }

    ngOnInit() {
        this.allTheTags = ["owner", "create", "pets", "vets"]; //TODO: thsi need to load from tests service
    }

    onTagsKeyUp(event) {
        if(event.key =="Enter") {
            if (this.currentTagSearch) {
                this.selectedTags.push(this.currentTagSearch);
                this.currentTagSearch = null;
                this.tagsAutoComplete.multiInputEL.nativeElement.value = null;
                event.preventDefault();
            }
        }
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
    }

    searchTags(event) {
        (((((this.treeModel.getChildren()[0] as JsonTreeContainer).getChildren()[1] as JsonTreeContainer).getChildren()[0] as JsonTreeContainer).getChildren()[1] as JsonTreeContainer).getChildren()[8] as JsonTreeContainer).setHidden(true);
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allTheTags,
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

    buttonStateChanged(event: any) {
        let oldIsAutomatedButtonActive = this.isAutomatedButtonActive;
        let oldIsManualButtonActive = this.isManualButtonActive;
        let oldIsSearchButtonActive = this.isSearchButtonActive;
        let oldIsTagsButtonActive = this.isTagsButtonActive;

        this.isAutomatedButtonActive = this.selectedTestTypesFilters.find(item => item.value == this.automatedButtonValue) != null;
        this.isManualButtonActive = this.selectedTestTypesFilters.find(item => item.value == this.manualButtonValue) != null;
        this.isSearchButtonActive = this.selectedTestTypesFilters.find(item => item.value == this.searchButtonValue) != null;
        this.isTagsButtonActive = this.selectedTestTypesFilters.find(item => item.value == this.tagsButtonValue) != null;

        if (oldIsAutomatedButtonActive != this.isAutomatedButtonActive) this.filter();
        if (oldIsManualButtonActive != this.isManualButtonActive) this.filter();
        if (oldIsSearchButtonActive != this.isSearchButtonActive && !this.isSearchButtonActive) this.filter();
        if (oldIsTagsButtonActive != this.isTagsButtonActive && !this.isTagsButtonActive) this.filter();
    }

    filter(): void {
        let featuresTreeFilter = new FeaturesTreeFilter();
        featuresTreeFilter.showAutomatedTests = this.isAutomatedButtonActive || this.isManualButtonActive == this.isAutomatedButtonActive;
        featuresTreeFilter.showManualTest = this.isManualButtonActive || this.isManualButtonActive == this.isAutomatedButtonActive;

        if (this.isSearchButtonActive) {
            featuresTreeFilter.search = this.searchText;
        }

        if (this.isTagsButtonActive) {
            featuresTreeFilter.tags = this.selectedTags;
        }

        this.featureTreeService.refreshTestTreeFromServer(featuresTreeFilter);
    }
}
