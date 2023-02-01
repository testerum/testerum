
//TODO Ionut: after inline see if we need to delete this
export interface FormPanelBody {

    saveAction(): void;

    cancelAction(): void;

    deleteAction(): void;

    isFormValid(): boolean;

    isEditMode(): boolean;

    isCreateNewResource():boolean;

    setEditMode(isEditMode:boolean): void;
}
