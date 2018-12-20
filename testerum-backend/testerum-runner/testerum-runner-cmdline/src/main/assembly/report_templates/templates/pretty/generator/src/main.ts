import {Application} from "./Application";

const dataFilePath = process.argv[2];
const properties=JSON.parse(process.argv[3]);

new Application(dataFilePath, properties).run();
