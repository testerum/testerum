import * as fs from 'fs';
import * as path from 'path';

export class FsUtils {

    static readFile(filePath: string): string {
        return fs.readFileSync(filePath, "utf8");
    }

    static writeFile(filePath: string, text: string) {
        // make sure parent directory exists
        let parentDirectory = path.resolve(filePath, "..");
        FsUtils.createDirectories(parentDirectory);

        fs.writeFileSync(filePath, text, "utf8");
    }

    /**
     * create all directories in the path, like ``mkdir -p``
     */
    static createDirectories(targetDir: string) {
        const sep = path.sep;
        const initDir = path.isAbsolute(targetDir) ? sep : '';
        const baseDir = '.';

        return targetDir.split(sep).reduce((parentDir, childDir) => {
            const curDir = path.resolve(baseDir, parentDir, childDir);
            try {
                fs.mkdirSync(curDir);
            } catch (err) {
                if (err.code === 'EEXIST') { // curDir already exists!
                    return curDir;
                }

                // To avoid `EISDIR` error on Mac and `EACCES`-->`ENOENT` and `EPERM` on Windows.
                if (err.code === 'ENOENT') { // Throw the original parentDir error on curDir `ENOENT` failure.
                    throw new Error(`EACCES: permission denied, mkdir '${parentDir}'`);
                }

                const caughtErr = ['EACCES', 'EPERM', 'EISDIR'].indexOf(err.code) > -1;
                if (!caughtErr || caughtErr && curDir === path.resolve(targetDir)) {
                    throw err; // Throw if it's just the last created dir.
                }
            }

            return curDir;
        }, initDir);
    }

}
