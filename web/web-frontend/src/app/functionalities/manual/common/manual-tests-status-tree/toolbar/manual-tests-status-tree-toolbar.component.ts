import {ChangeDetectionStrategy, Component, Input, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualTreeStatusFilterModel} from "../model/filter/manual-tree-status-filter.model";
import {JsonTreeExpandUtil} from "../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {ManualTestsStatusTreeService} from "../manual-tests-status-tree.service";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {TagsService} from "../../../../../service/tags.service";
import {AutoComplete} from "primeng/primeng";
import {ManualTestsStatusTreeNodeComponent} from "../nodes/runner-tree-node/manual-tests-status-tree-node.component";
import {ManualUiTreeBaseStatusModel} from "../model/manual-ui-tree-base-status.model";

@Component({
    selector: 'manual-tests-status-tree-toolbar',
    templateUrl: './manual-tests-status-tree-toolbar.component.html',
    styleUrls: ['./manual-tests-status-tree-toolbar.component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush, //under certain condition the app throws [Error: ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked. Previous value:] this is a fix
})
export class ManualTestsStatusTreeToolbarComponent implements OnInit {

    @Input() planPath: Path;
    @Input() treeModel:JsonTreeModel;

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    model = new ManualTreeStatusFilterModel();
    isSearchButtonActive = false;
    isTagsButtonActive = false;

    constructor(private manualTestsStatusTreeService: ManualTestsStatusTreeService,
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

    onToggleWaiting() {
        this.model.showNotExecuted = !this.model.showNotExecuted;
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
           filter.showPassed == false &&
           filter.showFailed == false &&
           filter.showBlocked == false) {

            filter.showNotExecuted = true;
            filter.showPassed = true;
            filter.showFailed = true;
            filter.showBlocked = true;
        }

        let testPath = Path.createInstanceOfEmptyPath();
        let selectedNode = this.treeModel.selectedNode as ManualUiTreeBaseStatusModel;
        if (selectedNode) {
            testPath = selectedNode.path;
        }
        this.manualTestsStatusTreeService.initializeTreeFromServer(this.planPath, testPath, filter);
    }

    onExpandAllNodes(): void {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel,  100);
    }

    onExpandToLevelEvent(level: number) {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel, level+1);
    }
}
