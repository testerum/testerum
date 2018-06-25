
import {EventEmitter, Injectable} from "@angular/core";
import {HttpRequest} from "../../../../../model/resource/http/http-request.model";
import {HttpContentType} from "../../../../../model/resource/http/enum/http-content-type.enum";
import {HttpRequestHeader} from "../../../../../model/resource/http/http-request-header.model";
import {HttpService} from "../../../../../service/resources/http/http.service";
import {HttpResponse} from "../../../../../model/resource/http/http-response.model";
import {HttpResponseVerify} from "./model/http-response-verify.model";
import {JsonVerifyTreeService} from "../../json_verify/json-verify-tree/json-verify-tree.service";
import {HttpBodyVerifyMatchingType} from "./model/enums/http-body-verify-matching-type.enum";
import {SerializationUtil} from "../../json_verify/json-verify-tree/model/util/serialization.util";

@Injectable()
export class HttpResponseVerifyService {

    model: HttpResponseVerify;
    editMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor(private jsonVerifyTreeService: JsonVerifyTreeService) {
        this.editModeEventEmitter.subscribe(
            (isEditMode: boolean) => this.jsonVerifyTreeService.editMode = isEditMode
        );
        this.jsonVerifyTreeService.editMode = this.editMode;
    }

    setHttpResponseVerify(httpResponseVerify: HttpResponseVerify): void {
        this.model ? Object.assign(this.model, httpResponseVerify) : this.model = httpResponseVerify;

        this.jsonVerifyTreeService.setEmpty();
        let expectedBody = this.model.expectedBody;
        if(expectedBody.httpBodyVerifyMatchingType == HttpBodyVerifyMatchingType.JSON_VERIFY &&
            expectedBody.bodyVerify) {

            let jsonVerifyAsJson = JSON.parse(expectedBody.bodyVerify);
            let jsonTreeNode = new SerializationUtil().deserialize(jsonVerifyAsJson);
            this.jsonVerifyTreeService.setJsonVerifyRootResource(jsonTreeNode);
        }
    }

    getModel(): HttpResponseVerify {
        return this.model;
    }

    empty() {
        Object.assign(this.model, new HttpResponseVerify());
    }

    setEditMode(editMode: boolean) {
        this.editMode = editMode;
        this.editModeEventEmitter.emit(this.editMode);
    }
}
