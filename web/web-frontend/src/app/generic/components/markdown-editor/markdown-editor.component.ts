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

    @Input() editMode: boolean = true;
    oldEditMode: boolean = true;

    @ViewChild('descriptionArea', { static: true }) textarea: ElementRef;
    simpleMDE: SimpleMDE;

    setValue(value: string, isCreateMode: boolean = false): void {
        if (this.simpleMDE) {
            if (this.simpleMDE.isPreviewActive()) {
                this.simpleMDE.togglePreview();
                this.simpleMDE.value(value ? value : "");
                this.simpleMDE.togglePreview();
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
            showIcons: ["bold","italic","strikethrough","heading","code","quote","unordered-list","ordered-list","link","image","table","horizontal-rule","preview","side-by-side","fullscreen","guide"],
            status: false,
            spellChecker: false,
            insertTexts: {
                table: ["", "\n\n| Column 1 | Column 2 | Column 3 |\n| ------------- | ------------- | ------------- |\n| Text          | Text          | Text          |\n\n"],
            },
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
                if (this.simpleMDE.isPreviewActive() == this.editMode) {
                    this.simpleMDE.togglePreview();
                }
                this.simpleMDE.gui.toolbar.hidden = !this.editMode;
                this.oldEditMode = this.editMode;
            }
        }
    }
}
