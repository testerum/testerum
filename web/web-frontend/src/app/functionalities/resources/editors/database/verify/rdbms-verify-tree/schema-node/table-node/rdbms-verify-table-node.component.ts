import {Component, Input, OnInit} from '@angular/core';
import {TableVerify} from "../../model/table-verify.model";
import {RdbmsVerifyTreeService} from "../../rdbms-verify-tree.service";
import {TableRowVerify} from "../../model/table-row-verify.model";
import {RdbmsTable} from "../../../../../../../../model/resource/rdbms/schema/rdbms-table.model";
import {CompareMode} from "../../../../../../../../model/enums/compare-mode.enum";

@Component({
    moduleId: module.id,
    selector: 'rdbms-verify-table-node',
    templateUrl: 'rdbms-verify-table-node.component.html',
    styleUrls:['../../rdbms-verify-tree.generic.css', '../../../../../../../../generic/css/tree.css']
})
export class RdbmsVerifyTableNodeComponent implements OnInit {

    @Input() model:TableVerify;

    showCompareMode:boolean = false;

    rdbmsVerifyTreeService:RdbmsVerifyTreeService;

    constructor(rdbmsVerifyTreeService:RdbmsVerifyTreeService) {
        this.rdbmsVerifyTreeService = rdbmsVerifyTreeService;
    }

    ngOnInit(): void {
        this.showCompareMode = this.model.compareMode != CompareMode.INHERIT
    }

    createVerifyRow() {
        let rdbmsTable: RdbmsTable = this.rdbmsVerifyTreeService.rdbmsSchema.getTableByName(this.model.name);

        let tableRowVerify = new TableRowVerify();
        if(rdbmsTable){
            tableRowVerify = TableRowVerify.createInstanceFromRdbmsTable(rdbmsTable)
        }

        this.model.rows.push(
            tableRowVerify
        )
    }

    deleteTable() {
        this.rdbmsVerifyTreeService.deleteTable(this.model)
    }

    onCompareModeChange(compareMode: CompareMode) {
        if(compareMode instanceof CompareMode) {
            this.model.compareMode = compareMode;
        }
    }

    shouldShowCompareMode(): boolean {
        if(this.rdbmsVerifyTreeService.editMode && this.model.compareMode != CompareMode.INHERIT) return true;

        return this.showCompareMode && this.model.rows.length != 0;
    }

    toggleShowCompareMode() {
        this.showCompareMode = !this.showCompareMode;
    }

    shouldShowCompareModeButton(): boolean {
        return this.model.rows.length != 0 && this.model.compareMode.getText() == CompareMode.INHERIT.getText();
    }
}
