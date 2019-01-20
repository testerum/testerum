import {Application} from "./Application";

const modelFile = process.argv[2];
const destinationDirectory = process.argv[3];

new Application(modelFile, destinationDirectory).run();
