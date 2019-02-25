import {Application} from "./Application";

const modelFile = process.argv[2];
const destinationDirectory = process.argv[3];

console.log(`[stats/generator] starting; modelFile=[${modelFile}], destinationDirectory=[${destinationDirectory}]`);

new Application(modelFile, destinationDirectory).run();
