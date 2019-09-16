import {ChangeDetectorRef, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {HttpRequestService} from "../http-request.service";
import {HttpRequestBody} from "../../../../../../model/resource/http/http-request-body.model";
import {HttpRequestBodyType} from "../../../../../../model/resource/http/enum/http-request-body-type.enum";
import {HttpContentType} from "../../../../../../model/resource/http/enum/http-content-type.enum";
import {Subscription} from "rxjs";
import {editor} from "monaco-editor";
import {MonacoEditorComponent} from "../../../../../../generic/components/monaco-editor/components/monaco-editor/monaco-editor.component";
import {JsonUtil} from "../../../../../../utils/json.util";

@Component({
    moduleId: module.id,
    selector: 'http-body',
    templateUrl: 'http-body.component.html',
    styleUrls: [
        'http-body.component.scss'
    ]
})
export class HttpBodyComponent implements OnInit, OnDestroy {

    @Input() body: HttpRequestBody;

    @ViewChild("monacoEditorComponent", { static: false }) monacoEditorComponent: MonacoEditorComponent;

    HttpBodyType = HttpRequestBodyType;
    HttpContentType = HttpContentType;

    editorMode = HttpContentType.TEXT.editorMode;
    isValidJson: boolean = true;

    editorOptions: editor.IEditorConstructionOptions = {
        language: this.editorMode,
        readOnly: !this.httpRequestService.editMode,

    };

    private changesMadeSubscription: Subscription;
    constructor(private cd: ChangeDetectorRef,
                public httpRequestService: HttpRequestService) {
    }

    ngOnInit(): void {
        this.changesMadeSubscription = this.httpRequestService.changesMadeEventEmitter.subscribe(it => {
            this.onHeaderChange();
        });
        this.httpRequestService.editModeEventEmitter.subscribe(editModeEvent => {
            this.refreshEditorOption();
        });
        this.editorMode = this.getContentType().editorMode;

        this.refresh();
    }

    ngOnDestroy(): void {
        if(this.changesMadeSubscription) this.changesMadeSubscription.unsubscribe();
    }

    refresh() {
        if (!this.cd['destroyed']) { //without this the folowing error will appear: "ERROR Error: ViewDestroyedError: Attempt to use a destroyed view: detectChanges"
            this.cd.detectChanges();
        }
    }

    refreshEditorOption(): void {

        if (this.editorMode == HttpContentType.JSON.editorMode) {
            this.isValidJson = JsonUtil.isJson(this.body.content);
        }

        this.editorOptions = Object.assign(
            {},
            this.editorOptions,
            {
                language: this.editorMode,
                readOnly: !this.httpRequestService.editMode
            }
        );
    }

    setContentType(contentType:HttpContentType) {
        this.httpRequestService.setContentType(contentType);
        this.editorMode = contentType.editorMode;
        this.refreshEditorOption();
    }

    getContentType(): HttpContentType {
        let contentType = this.httpRequestService.getContentType();
        if(!contentType) {
            return HttpContentType.TEXT;
        }

        return contentType;
    }

    setHeaderBasedOnBodyType(event: any) {
        this.httpRequestService.setContentTypeAsString(this.body.bodyType.contentTypeHeaderValue);
    }

    onHeaderChange() {
        let contentType = this.getContentType();
        this.editorMode = contentType.editorMode;
        this.refreshEditorOption();
    }

    onTextChange(text: string) {
        this.body.content = text;
        if (this.editorMode == HttpContentType.JSON.editorMode) {
            this.isValidJson = JsonUtil.isJson(text);
        }
    }

    onFormatJsonEvent() {
        let editor = this.monacoEditorComponent.editor;
        setTimeout(function() {
            editor.getAction('editor.action.formatDocument').run();
        }, 300);
    }

    shouldDisplayFormatButton(): boolean {
        return this.editorMode != HttpContentType.TEXT.editorMode
            && this.editorMode != HttpContentType.XML.editorMode
            && this.editorMode != HttpContentType.XML_TEXT.editorMode;
    }

    shouldDisplayJsonValidation(): boolean {
        return this.editorMode == HttpContentType.JSON.editorMode
    }
}
