import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {TestTreeNodeModel} from "../../model/test-tree-node.model";
import {UrlService} from "../../../../../service/url.service";

@Component({
    moduleId: module.id,
    selector: 'json-test-node',
    templateUrl: 'test-node.component.html',
    styleUrls:['test-node.component.scss']
})
export class TestNodeComponent {

    @Input() model:TestTreeNodeModel;

    constructor(private router: Router,
                private urlService: UrlService) {
    }

    setSelected() {
        this.urlService.navigateToTest(this.model.path);
    }
}
