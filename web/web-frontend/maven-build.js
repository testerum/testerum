let proc = require('child_process');

let os = require('os');

function getOs() {
  let osType = os.type();

  if (osType === "Linux") {
    return "linux";
  } else if (osType === "Darwin") {
    return "mac";
  } else if (osType === "Windows_NT") {
    return "windows";
  } else {
    throw new Error("Unsupported OS found: [" + osType + "]");
  }
}

let osBinaries = {
  "linux": {
    "ng": "node_modules/.bin/ng"
  },
  "mac": {
    "ng": "node_modules/.bin/ng"
  },
  "windows": {
    "ng": "node_modules\\.bin\\ng.cmd"
  }
};

let osType = getOs();
let ngBinary = osBinaries[osType]["ng"];

var env = Object.create( process.env );

if (osType === "windows") {
  env.PATH = 'node;' + env.PATH;
} else {
  env.PATH = 'node:' + env.PATH;
}

let child = proc.spawn(ngBinary, ["build", "--prod", "--source-map"], { env: env });

child.stdout.on('data', function (data) {
  console.log('stdout: ' + data);
});
child.stderr.on('data', function (data) {
  console.log('stderr: ' + data);
});
child.on('close', function (code) {
  console.log('child process exited with code' + code);
  process.exit(code);
});
