import {JsonUtil} from "../../../../../../../utils/json.util";
import {HttpMockResponseType} from "./enums/http-mock-response-type.enum";
import {HttpMockRequest} from "./request/http-mock-request.model";
import {HttpMockResponse} from "./response/http-mock-response.model";
import {HttpMockFaultResponse} from "./enums/http-mock-fault-response.enum";
import {HttpMockProxyResponse} from "./response/http-mock-proxy-response.model";
import {Resource} from "../../../../../../../model/resource/resource.model";

export class HttpMock implements Resource<HttpMock> {

    expectedRequest: HttpMockRequest = new HttpMockRequest();

    selectedResponse: HttpMockResponseType = HttpMockResponseType.MOCK;
    mockResponse: HttpMockResponse = new HttpMockResponse();
    faultResponse:HttpMockFaultResponse = HttpMockFaultResponse.NO_FAULT;
    proxyResponse:HttpMockProxyResponse = new HttpMockProxyResponse();

    constructor() {
        this.reset();
    }

    reset(): void {
        this.expectedRequest.reset();

        this.selectedResponse = HttpMockResponseType.MOCK;
        this.mockResponse.reset();
        this.faultResponse = HttpMockFaultResponse.NO_FAULT;
        this.proxyResponse.reset();
    }

    isEmpty(): boolean {
        return this.expectedRequest.isEmpty();
    }

    deserialize(input: Object): HttpMock {

        if(input['expectedRequest']) {
            this.expectedRequest = this.expectedRequest.deserialize(input['expectedRequest'])
        }

        if(input['mockResponse']) {
            this.selectedResponse = HttpMockResponseType.MOCK;
            this.mockResponse = new HttpMockResponse().deserialize(input['mockResponse'])
        }

        if(input['faultResponse']) {
            this.selectedResponse = HttpMockResponseType.FAULT;
            this.faultResponse = HttpMockFaultResponse.fromSerialization(input['faultResponse'])
        }

        if(input['proxyResponse']) {
            this.selectedResponse = HttpMockResponseType.PROXY;
            this.proxyResponse = new HttpMockProxyResponse().deserialize(input['proxyResponse'])
        }

        return this;
    }

    serialize(): string {

        let result = '{';

        result += '"expectedRequest":' + JsonUtil.serializeSerializable(this.expectedRequest);

        if (this.selectedResponse == HttpMockResponseType.MOCK) {
            result += ',"mockResponse":' + JsonUtil.serializeSerializable(this.mockResponse);
        }

        if (this.faultResponse
            && this.faultResponse != HttpMockFaultResponse.NO_FAULT
            && this.selectedResponse == HttpMockResponseType.FAULT) {
            result += ',"faultResponse":' + JsonUtil.stringify(this.faultResponse.asSerialized);
        }

        if (this.selectedResponse == HttpMockResponseType.PROXY) {
            result += ',"proxyResponse":' + JsonUtil.serializeSerializable(this.proxyResponse);
        }

        result += '}';
        return result;
    }

    clone(): HttpMock {
        let objectAsJson = JSON.parse(this.serialize());
        return new HttpMock().deserialize(objectAsJson);
    }

    createInstance(): HttpMock {
        return new HttpMock();
    }
}
