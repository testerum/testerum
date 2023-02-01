
import {Enum} from "../../../../../../../../model/enums/enum.interface";

export class HttpMockFaultResponse extends Enum {

    public static NO_FAULT = new HttpMockFaultResponse("NO_FAULT", "No fault");
    public static CLOSE_SOCKET_WITH_NO_RESPONSE = new HttpMockFaultResponse("CLOSE_SOCKET_WITH_NO_RESPONSE", "Close socket with no response");
    public static SEND_BAD_HTTP_DATA_THEN_CLOSE_SOCKET = new HttpMockFaultResponse("SEND_BAD_HTTP_DATA_THEN_CLOSE_SOCKET", "Send bad HTTP data then close socket");
    public static SEND_200_RESPONSE_THEN_BAD_HTTP_DATA_THEN_CLOSE_SOCKET = new HttpMockFaultResponse("SEND_200_RESPONSE_THEN_BAD_HTTP_DATA_THEN_CLOSE_SOCKET", "Send 200 response then bad HTTP data, then close socket");
    public static PEER_CONNECTION_RESET = new HttpMockFaultResponse("PEER_CONNECTION_RESET", "Peer connection reset");

    static readonly enums: Array<HttpMockFaultResponse> = [
        HttpMockFaultResponse.NO_FAULT,
        HttpMockFaultResponse.CLOSE_SOCKET_WITH_NO_RESPONSE,
        HttpMockFaultResponse.SEND_BAD_HTTP_DATA_THEN_CLOSE_SOCKET,
        HttpMockFaultResponse.SEND_200_RESPONSE_THEN_BAD_HTTP_DATA_THEN_CLOSE_SOCKET,
        HttpMockFaultResponse.PEER_CONNECTION_RESET,
    ];

    public readonly asSerialized: string;
    private constructor(asSerialized: string, asString: string) {
        super(asString);
        this.asSerialized = asSerialized;
    }

    public static fromString(methodAsString: string): HttpMockFaultResponse {
        for (let enumValue of HttpMockFaultResponse.enums) {
            if(enumValue.enumAsString.toUpperCase() == methodAsString.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }

    public static fromSerialization(asSerialized: string): HttpMockFaultResponse {
        for (let enumValue of HttpMockFaultResponse.enums) {
            if(enumValue.asSerialized.toUpperCase() == asSerialized.toUpperCase()) {
                return enumValue;
            }
        }

        return null;
    }
}
