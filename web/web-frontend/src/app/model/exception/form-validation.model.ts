
export class FormValidationModel implements Serializable<FormValidationModel> {

    globalValidationMessage: string;
    globalValidationMessageDetails: string;
    fieldsWithValidationErrors: Map<string, string> = new Map<string, string>();


    deserialize(input: Object): FormValidationModel {

        this.globalValidationMessage = input["globalValidationMessage"];
        this.globalValidationMessageDetails = input["globalValidationMessageDetails"];
        let inputFieldsWithValidationErrors = input["fieldsWithValidationErrors"];
        Object.keys(inputFieldsWithValidationErrors).forEach( key => {
            let value = inputFieldsWithValidationErrors[key];
            this.fieldsWithValidationErrors.set(key, value);
        });

        return this;
    }

    serialize(): string {
        return null;
    }

    static createaInstanceFromJson( json: any): FormValidationModel {
        let validationException = new FormValidationModel();

        validationException.globalValidationMessage = json["globalValidationMessage"];
        validationException.globalValidationMessageDetails = json["globalValidationMessageDetails"];
        validationException.fieldsWithValidationErrors = json["fieldsWithValidationErrors"];

        return validationException;
    }
}
