import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {IdUtils} from "../../../../../utils/id.util";
import {BasicResource} from "../../../../../model/resource/basic/basic-resource.model";
import {ParamStepPatternPart} from "../../../../../model/text/parts/param-step-pattern-part.model";
import {EnumTypeMeta} from "../../../../../model/text/parts/param-meta/enum-type.meta";

@Component({
  selector: 'enum-input-basic-resource',
  templateUrl: './enum-input-basic-resource.component.html',
  styleUrls: ['./enum-input-basic-resource.component.scss']
})
export class EnumInputBasicResourceComponent implements OnInit {

    @Input() name: string;
    @Input() model: BasicResource;
    @Input() stepParameter?: ParamStepPatternPart;
    @Input() editMode: boolean = false;
    @Input() condensedViewMode: boolean = false;
    @Input() contextActions: ResourceContextActions;

    @ViewChild('enumInput', { static: false }) inputElementRef: ElementRef;
    private tempValueHolder: string;

    id = IdUtils.getTemporaryId();
    possibleValues: string[] = [];

    ngOnInit(): void {
        let enumTypeMeta = this.stepParameter.serverType as EnumTypeMeta;
        if (enumTypeMeta) {
            for (const possibleValue of enumTypeMeta.possibleValues) {
                this.possibleValues.push(possibleValue)
            }
        }
    }

    onValueChange(newValue: string) {
        this.model.content = newValue;
    }

    onInputEvent(event: MouseEvent) {
        this.tempValueHolder = this.inputElementRef.nativeElement.value;
        this.inputElementRef.nativeElement.value = '';
        let that = this;
        setTimeout(() => {
            this.inputElementRef.nativeElement.value = this.tempValueHolder;
        }, 10);
    }
}
