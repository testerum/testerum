import {Component, Input, OnInit} from '@angular/core';
import {TableRowVerify} from "../../../model/table-row-verify.model";
import {CompareMode} from "../../../../../../../../../model/enums/compare-mode.enum";
import {RdbmsVerifyTreeService} from "../../../rdbms-verify-tree.service";

@Component({
    moduleId: module.id,
    selector: 'json-verify-table-row-node',
    templateUrl: 'rdbms-verify-table-row-node.component.html',
    styleUrls:['../../../../../../../../../generic/css/tree.css']
})
export class RdbmsVerifyTableRowNodeComponent implements OnInit {

    @Input() model:TableRowVerify;

    showCompareMode:boolean = false;

    rdbmsVerifyTreeService:RdbmsVerifyTreeService;

    constructor(rdbmsVerifyTreeService:RdbmsVerifyTreeService) {
        this.rdbmsVerifyTreeService = rdbmsVerifyTreeService;
    }

    ngOnInit(): void {
        this.showCompareMode = this.model.compareMode != CompareMode.INHERIT
    }

    onCompareModeChange(compareMode: CompareMode) {
        if(compareMode instanceof CompareMode) {
            this.model.compareMode = compareMode;
        }
    }

    shouldShowCompareMode(): boolean {
        if(this.rdbmsVerifyTreeService.editMode && this.model.compareMode != CompareMode.INHERIT) return true;
        return this.showCompareMode;
    }

    toggleShowCompareMode() {
        this.showCompareMode = !this.showCompareMode;
    }

    shouldShowCompareModeButton(): boolean {
        return this.model.compareMode == CompareMode.INHERIT;
    }

    deleteRow() {
        this.rdbmsVerifyTreeService.deleteTableRow(this.model)
    }
}
