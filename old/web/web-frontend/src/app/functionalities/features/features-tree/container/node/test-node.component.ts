import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {TestTreeNodeModel} from "../../model/test-tree-node.model";
import {UrlService} from "../../../../../service/url.service";
import {FeaturesTreeService} from "../../features-tree.service";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";
import {TestsRunnerService} from "../../../tests-runner/tests-runner.service";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'test-node.component.html',
    styleUrls:['test-node.component.scss']
})
export class TestNodeComponent {

    @Input() model:TestTreeNodeModel;

    hasMouseOver: boolean = false;

    constructor(private router: Router,
                private urlService: UrlService,
                private jsonTreeService: JsonTreeService,
                private testsRunnerService: TestsRunnerService,
                private featuresTreeService: FeaturesTreeService) {
    }

    setSelected() {
        this.urlService.navigateToTest(this.model.path);
    }

    onCutFeature() {
        this.setSelected();
        this.featuresTreeService.setPathToCut(this.model.path);
    }

    onCopyFeature() {
        this.setSelected();
        this.featuresTreeService.setPathToCopy(this.model.path);
    }

    runTests() {
        this.testsRunnerService.runTests([this.model.path]);
    }
}
