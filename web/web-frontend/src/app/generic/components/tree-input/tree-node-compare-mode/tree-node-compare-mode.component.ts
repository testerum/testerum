import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CompareMode} from "../../../../model/enums/compare-mode.enum";

@Component({
    moduleId: module.id,
    selector: 'tree-node-compare-mode',
    templateUrl: 'tree-node-compare-mode.component.html',
    styleUrls:["/tree-node-compare-mode.component.scss", "../../../css/tree.scss"]
})

export class TreeNodeCompareModeComponent {

    @Input() selectedCompareMode = CompareMode.CONTAINS;
    @Input() editMode: boolean;
    @Output() change: EventEmitter<CompareMode> = new EventEmitter();
    CompareMode=CompareMode;

    getAllCompareModes(): Array<CompareMode> {
        return CompareMode.getAllCompareModes();
    }

    setCompareMode(mode: string) {
        this.selectedCompareMode = CompareMode.getByText(mode);
        this.change.emit(this.selectedCompareMode);
    }
}
