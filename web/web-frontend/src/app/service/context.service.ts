import {Injectable} from "@angular/core";
import {Path} from "../model/infrastructure/path/path.model";

@Injectable()
export class ContextService {

    stepPathToCut: Path = null;
    stepPathToCopy: Path = null;

    setPathToCut(path: Path) {
        this.pathToCopy = null;
        this.pathToCut = path;
    }

    setPathToCopy(path: Path) {
        this.pathToCopy = path;
        this.pathToCut = null;
    }

    canPaste(path: Path): boolean {
        return (this.pathToCopy != null && !this.pathToCopy.equals(path))
            || (this.pathToCut != null && !this.pathToCut.equals(path));
    }
}
