import {ChangeDetectionStrategy, Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {RunnerConfigTestTreeFilterModel} from "../model/filter/runner-config-test-tree-filter.model";
import {RunnerConfigTestTreeService} from "../runner-config-test-tree.service";
import {AutoComplete} from "primeng/primeng";
import {RunnerConfigTestTreeBaseModel} from "../model/runner-config-test-tree-base.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeModel} from "../../../../../../../generic/components/json-tree/model/json-tree.model";
import {TagsService} from "../../../../../../../service/tags.service";
import {ArrayUtil} from "../../../../../../../utils/array.util";
import {JsonTreeExpandUtil} from "../../../../../../../generic/components/json-tree/util/json-tree-expand.util";

@Component({
    selector: 'manual-tests-status-tree-toolbar',
    templateUrl: './runner-config-test-toolbar.component.html',
    styleUrls: ['./runner-config-test-toolbar.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class RunnerConfigTestTreeToolbarComponent implements OnInit {

    @Input() planPath: Path;
    @Input() treeModel: JsonTreeModel;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    model = new RunnerConfigTestTreeFilterModel();
    isSearchButtonActive = false;
    isTagsButtonActive = false;

    constructor(private manualTestsStatusTreeService: RunnerConfigTestTreeService,
                private tagsService: TagsService) {}

    ngOnInit() {}

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
        for (let currentTag of this.model.tags) {
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

    onToggleNotExecuted() {
        this.model.showNotExecuted = !this.model.showNotExecuted;
        this.filter();
    }

    onToggleInProgress() {
        this.model.showInProgress = !this.model.showInProgress;
        this.filter();
    }

    onTogglePassed() {
        this.model.showPassed = !this.model.showPassed;
        this.filter();
    }

    onToggleFailed() {
        this.model.showFailed = !this.model.showFailed;
        this.filter();
    }

    onToggleBlocked() {
        this.model.showBlocked = !this.model.showBlocked;
        this.filter();
    }

    onToggleNotApplicable() {
        this.model.showNotApplicable = !this.model.showNotApplicable;
        this.filter();
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

    private filter() {
        let filter = this.model.clone();

        if(filter.showNotExecuted  == false &&
           // filter.showInProgress == false &&
           filter.showPassed == false &&
           filter.showFailed == false &&
           filter.showBlocked == false &&
           filter.showNotApplicable == false) {

            filter.showNotExecuted = true;
            // filter.showInProgress = true;
            filter.showPassed = true;
            filter.showFailed = true;
            filter.showBlocked = true;
            filter.showNotApplicable = true;
        }

        let testPath = Path.createInstanceOfEmptyPath();
        let selectedNode = this.treeModel.selectedNode as RunnerConfigTestTreeBaseModel;
        if (selectedNode) {
            testPath = selectedNode.path;
        }
        // this.manualTestsStatusTreeService.initializeTreeFromServer(filter);
    }

    onExpandAllNodes(): void {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel,  100);
    }

    onExpandToLevelEvent(level: number) {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, level+1);
    }
}
