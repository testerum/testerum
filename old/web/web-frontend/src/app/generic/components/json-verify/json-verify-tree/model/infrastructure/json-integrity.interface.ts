
export interface JsonIntegrity {

    isDirty: boolean;
    isEmptyAndShouldNotBeSaved(): boolean;
    isValid(): boolean;
}
