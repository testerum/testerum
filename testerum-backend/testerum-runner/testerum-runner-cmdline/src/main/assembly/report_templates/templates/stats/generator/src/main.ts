import {Application} from "./Application";
import {RunnerPropertiesParser} from "../../../../common/util/RunnerPropertiesParser";

const modelDirectory = process.argv[2];
const properties = RunnerPropertiesParser.parse(process.argv[3]);

new Application(modelDirectory, properties).run();
