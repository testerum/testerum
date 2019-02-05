import {Application} from "./Application";
import {RunnerPropertiesParser} from "../../../../common/util/RunnerPropertiesParser";

const modelDirectory = process.argv[2];
const properties = RunnerPropertiesParser.parse(process.argv[3]);

console.log(`[app/generator] starting; modelDirectory=[${modelDirectory}], properties=[${JSON.stringify(properties)}]`);

new Application(modelDirectory, properties).run();
