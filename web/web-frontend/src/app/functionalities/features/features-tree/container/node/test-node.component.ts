import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {TestTreeNodeModel} from "../../model/test-tree-node.model";
import {UrlService} from "../../../../../service/url.service";
import {JsonTreeContainerEditorEvent} from "../../../../../generic/components/json-tree/container-editor/model/json-tree-container-editor.event";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {FeaturesTreeService} from "../../features-tree.service";
import {FeatureService} from "../../../../../service/feature.service";
import {JsonTreeService} from "../../../../../generic/components/json-tree/json-tree.service";

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
}
