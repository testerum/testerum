
export class FlowUtil {

    static delay(ms: number) {
        return new Promise( resolve => setTimeout(resolve, ms) );
    }
}
