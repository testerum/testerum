export class TestProperties implements Serializable<TestProperties> {

    isManual: boolean = false;
    isDisabled: boolean = false;

    deserialize(input: Object): TestProperties {
        this.isManual = input['isManual'];
        this.isDisabled = input['isDisabled'];

        return this;
    }

    serialize() {
        return "" +
            '{' +
            '"isManual":' + this.isManual + ',' +
            '"isDisabled":' + this.isDisabled +
            '}'
    }
}
