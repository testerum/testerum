import {Component, OnInit} from '@angular/core';
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {ActivatedRoute} from "@angular/router";
import {BasicStepDef} from "../../../model/basic-step-def.model";

@Component({
    moduleId: module.id,
    selector: 'basic-step-editor',
    templateUrl: 'basic-step-editor.component.html',
    styleUrls: ['./basic-step-editor.component.scss']
})

export class BasicStepEditorComponent implements OnInit {

    StepPhaseEnum = StepPhaseEnum;

    model: BasicStepDef = new BasicStepDef();

    pattern: string;

    constructor(private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.route.data.subscribe(data => {
            this.model = data['basicStepDef'];
            this.pattern = this.model.stepPattern.getPatternText();
        });
    }

    getJavaClass(): string {
        return this.model.path.directories.join(".") + "." + this.model.path.fileName
    }

    getJavaMethod(): string {
        return this.model.path.fileExtension.split("(")[0];
    }
}
