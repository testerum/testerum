
import {Path} from "../../../model/infrastructure/path/path.model";
import {FormPanelBody} from "./infrastructure/form-panel-container/model/form-panel-body.model";

//TODO: deprecated
export interface ResourceEditorInterface extends FormPanelBody {

    initInCreateMode(): void;

    initFromResourcePath(resourcePath: Path): void;
}
