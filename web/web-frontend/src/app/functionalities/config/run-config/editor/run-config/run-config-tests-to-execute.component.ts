import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {RunConfig} from "../../model/runner-config.model";
import {AutoComplete} from "primeng/primeng";
import {ArrayUtil} from "../../../../../utils/array.util";
import {TagsService} from "../../../../../service/tags.service";

@Component({
    selector: 'run-config-tests-to-execute',
    templateUrl: './run-config-tests-to-execute.component.html',
    styleUrls: ['./run-config-tests-to-execute.component.scss'],
})
export class RunConfigTestsToExecuteComponent implements OnInit {

    @Input() runnerConfig: RunConfig;

    allKnownTags: Array<string> = [];
    @ViewChild("tagsToIncludeElement") tagsExecuteAutoComplete: AutoComplete;
    tagsToIncludeToShow:string[] = [];
    currentToExecuteTagSearch:string;
    @ViewChild("tagsToExcludeElement") tagsExcludeAutoComplete: AutoComplete;
    tagsToExcludeToShow:string[] = [];
    currentToExcludeTagSearch:string;


    constructor(private tagsService: TagsService) {
    }

    ngOnInit() {
        this.tagsService.getTags().subscribe(tags => {
            ArrayUtil.replaceElementsInArray(this.allKnownTags, tags);
        });
    }

    //start methods for TAGS TO EXECUTE
    onSearchTagsToInclude(event) {
        this.currentToExecuteTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.runnerConfig.tagsToInclude) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        for (let currentTag of this.runnerConfig.tagsToExclude) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToIncludeToShow = newTagsToShow
    }

    onTagsToIncludeKeyUp(event: KeyboardEvent) {
        event.preventDefault();

        if (event.key == "Enter") {
            this.addCurrentTagToTagsToInclude();
        }
    }

    addCurrentTagToTagsToInclude() {
        if (this.currentToExecuteTagSearch) {
            this.runnerConfig.tagsToInclude.push(this.currentToExecuteTagSearch);
            this.currentToExecuteTagSearch = null;
            this.tagsExecuteAutoComplete.multiInputEL.nativeElement.value = null;
        }
    }

    onTagsToIncludeSelect(event) {
        this.currentToExecuteTagSearch = null;
    }
    //end methods for TAGS TO EXECUTE

    //start methods for TAGS TO EXCLUDE
    onSearchTagToExclude(event) {
        this.currentToExcludeTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.runnerConfig.tagsToExclude) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        for (let currentTag of this.runnerConfig.tagsToInclude) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToExcludeToShow = newTagsToShow
    }

    onTagsToExcludeKeyUp(event: KeyboardEvent) {
        event.preventDefault();

        if (event.key == "Enter") {
            this.addCurrentTagToTagsToExclude();
        }
    }

    addCurrentTagToTagsToExclude() {
        if (this.currentToExcludeTagSearch) {
            this.runnerConfig.tagsToExclude.push(this.currentToExcludeTagSearch);
            this.currentToExcludeTagSearch = null;
            this.tagsExcludeAutoComplete.multiInputEL.nativeElement.value = null;
        }
    }

    onTagToExcludeSelect(event) {
        this.currentToExcludeTagSearch = null;
    }
    //end methods for TAGS TO EXCLUDE

}
