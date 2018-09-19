import {AfterViewInit, Component, ElementRef, EventEmitter, Input, ViewChild, ViewEncapsulation} from '@angular/core';
import * as SimpleMDE from 'simplemde'

@Component({
    selector: 'markdown-editor',
    templateUrl: 'markdown-editor.component.html',
    styleUrls: ['markdown-editor.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class MarkdownEditorComponent implements AfterViewInit {

    changeEventEmitter: EventEmitter<string> = new EventEmitter<string>();

    private value: string; //did this with setter and getters because binding this value on update is not working

    @Input() options: any;
    @Input() editMode: boolean = true;
    oldEditMode: boolean = true;

    @ViewChild('descriptionArea') textarea: ElementRef;
    simpleMDE: SimpleMDE;

    setValue(value: string): void {
        if (this.simpleMDE) {
            if (!this.editMode) {
                this.setEditMode(true);
                this.simpleMDE.value(value ? value : "");
                this.setEditMode(false);
            } else {
                this.simpleMDE.value(value ? value : "");
            }
        }
        this.value = value;
    }

    getValue(): string {
        if (!this.simpleMDE) {
            return null;
        }
        return this.simpleMDE.value();
    }

    ngAfterViewInit(): void {
        this.initMarkdownEditor()
    }

    initMarkdownEditor() {
        const config = {
            ...this.options,
            element: this.textarea.nativeElement
        };
        this.simpleMDE = new SimpleMDE(config);

        if (this.value) {
            this.simpleMDE.value(this.value);
        }
        let that = this;
        this.simpleMDE.codemirror.on("change", function(){
            that.changeEventEmitter.emit(
                that.getValue()
            )
        });

        this.handleEditModeChanged();
    }

    setEditMode(value: boolean) {
        this.editMode = value;
        this.handleEditModeChanged()
    }

    private handleEditModeChanged() {
        if (this.editMode != this.oldEditMode) {
            if (this.simpleMDE) {
                this.simpleMDE.togglePreview();
                this.simpleMDE.gui.toolbar.hidden = !this.editMode;
                this.oldEditMode = this.editMode;
            }
        }
    }
}
