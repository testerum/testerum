import {Component, OnInit} from '@angular/core';
import {ResultService} from "../../../service/report/result.service";
import {RunnerResultDirInfo} from "../../../model/report/runner-result-dir-info.model";
import {ResultsTreeContainerModel} from "./model/results-tree-container.model";
import {ResultsTreeNodeModel} from "./model/results-tree-node.model";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {ResultsTreeContainerComponent} from "./container/results-tree-container.component";
import {ResultsTreeNodeComponent} from "./container/leaf/results-tree-node.component";
import {JsonTreeModel} from "../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeContainerAbstract} from "../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeContainer} from "../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {JsonTreeExpandUtil} from "../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeService} from "../../../generic/components/json-tree/json-tree.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'results-tree',
    templateUrl: 'results-tree.component.html'
})

export class ResultsTreeComponent implements OnInit {

    directoryTreeModel: JsonTreeModel = new JsonTreeModel();

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ResultsTreeContainerModel, ResultsTreeContainerComponent)
        .addPair(ResultsTreeNodeModel, ResultsTreeNodeComponent);

    constructor(private resultService: ResultService,
                private jsonTreeService: JsonTreeService,
                private activatedRoute: ActivatedRoute,) {
    }

    ngOnInit() {
        let pathAsString = this.activatedRoute.firstChild ? this.activatedRoute.firstChild.snapshot.params['path']: null;
        let path: Path = pathAsString !=null ? Path.createInstance(pathAsString) : null;

        this.resultService.getRunnerReportDirInfo().subscribe(
            (dirs: Array<RunnerResultDirInfo>) => {
                this.directoryTreeModel.children.length = 0;
                for (let dir of dirs) {
                    this.directoryTreeModel.children.push(
                        ResultsTreeComponent.mapToTreeModel(dir, this.directoryTreeModel)
                    )
                }
                this.directoryTreeModel.children.sort((a,b) => (a as ResultsTreeContainerModel).name > (b as ResultsTreeContainerModel).name ? -1 : 1);
                if(this.directoryTreeModel.children.length > 0) {
                    (this.directoryTreeModel.children[0] as JsonTreeContainerAbstract).jsonTreeNodeState.showChildren = true;
                }

                for (const resultDir of this.directoryTreeModel.children) {
                    (resultDir as ResultsTreeContainerModel).resultFiles.sort((a, b) => (a as ResultsTreeNodeModel).name > (b as ResultsTreeNodeModel).name ? -1 : 1)
                }

                let selectedNode = JsonTreeExpandUtil.expandTreeToPathAndReturnNode(this.directoryTreeModel, path);
                this.jsonTreeService.setSelectedNode(selectedNode);

            }
        )
    }

    private static mapToTreeModel(runnerResultDirInfo: RunnerResultDirInfo, parentContainer: JsonTreeContainer): ResultsTreeContainerModel {

        let result = new ResultsTreeContainerModel(parentContainer);
        result.jsonTreeNodeState.showChildren = false;

        result.name = runnerResultDirInfo.directoryName;

        for (let file of runnerResultDirInfo.runnerResultFilesInfo) {
            let treeFile = new ResultsTreeNodeModel(result);
            treeFile.path = file.path;
            treeFile.name = file.name;
            treeFile.url = file.url;
            treeFile.executionResult = file.executionResult;
            treeFile.durationMillis = file.durationMillis;

            result.resultFiles.push(
                treeFile
            )
        }

        return result
    }
}
