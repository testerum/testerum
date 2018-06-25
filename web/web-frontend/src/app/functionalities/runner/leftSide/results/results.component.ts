///<reference path="../../../../../../node_modules/@angular/core/src/metadata/directives.d.ts"/>
import {Component, OnInit} from '@angular/core';
import {ResultService} from "../../../../service/report/result.service";
import {RunnerResultDirInfo} from "../../../../model/report/runner-result-dir-info.model";
import {ResultsDirectory} from "./model/results-directory.model";
import {ResultFile} from "./model/result-file.model";
import {ModelComponentMapping} from "../../../../model/infrastructure/model-component-mapping.model";
import {ResultDirectoryComponent} from "./container/result-directory.component";
import {ResultFileComponent} from "./container/leaf/result-file.component";
import {JsonTreeModel} from "../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeContainerAbstract} from "../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";

@Component({
    selector: 'results',
    templateUrl: 'results.component.html'
})

export class ResultsComponent implements OnInit {

    directoryTreeModel: JsonTreeModel = new JsonTreeModel();

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ResultsDirectory, ResultDirectoryComponent)
        .addPair(ResultFile, ResultFileComponent);

    constructor(private resultService: ResultService) {
    }

    ngOnInit() {
        this.resultService.getRunnerReportDirInfo().subscribe(
            (results: Array<RunnerResultDirInfo>) => {
                this.directoryTreeModel.children.length = 0;
                for (let result of results) {
                    this.directoryTreeModel.children.push(
                        ResultsComponent.mapToTreeModel(result, this.directoryTreeModel)
                    )
                }
                this.directoryTreeModel.children.sort((a,b) => (a as ResultsDirectory).name > (b as ResultsDirectory).name ? -1 : 1);
                if(this.directoryTreeModel.children.length > 0) {
                    (this.directoryTreeModel.children[0] as JsonTreeContainerAbstract).jsonTreeNodeState.showChildren = true;
                }

                for (const resultDir of this.directoryTreeModel.children) {
                    (resultDir as ResultsDirectory).resultFiles.sort((a,b) => (a as ResultFile).name > (b as ResultFile).name ? -1 : 1)
                }
            }
        )
    }

    private static mapToTreeModel(runnerResultDirInfo: RunnerResultDirInfo, parentContainer: JsonTreeContainer): ResultsDirectory {

        let result = new ResultsDirectory(parentContainer);
        result.jsonTreeNodeState.showChildren = false;

        result.name = runnerResultDirInfo.directoryName;

        for (let file of runnerResultDirInfo.runnerResultFilesInfo) {
            let treeFile = new ResultFile(result);
            treeFile.path = file.path;
            treeFile.name = file.name;
            treeFile.executionResult = file.executionResult;
            treeFile.durationMillis = file.durationMillis;

            result.resultFiles.push(
                treeFile
            )
        }

        return result
    }
}
