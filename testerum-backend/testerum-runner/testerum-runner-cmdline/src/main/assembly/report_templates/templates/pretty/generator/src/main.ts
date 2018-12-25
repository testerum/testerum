import {Application} from "./Application";

const modelDirectory = process.argv[2];
const properties=JSON.parse(process.argv[3]);

new Application(modelDirectory, properties).run();
