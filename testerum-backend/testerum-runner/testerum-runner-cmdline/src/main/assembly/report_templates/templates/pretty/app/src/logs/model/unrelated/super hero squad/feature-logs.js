receiveModel([{"time":"2018-12-27T11:13:07.013","logLevel":"INFO","message":"Executing feature super hero squad"},{"time":"2018-12-27T11:13:07.014","logLevel":"INFO","message":"Executing test \"Eternal Flame\" is immortal"},{"time":"2018-12-27T11:13:07.75","logLevel":"INFO","message":"11:13:07.017 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T11:13:07.75","logLevel":"INFO","message":"11:13:07.749 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T11:13:07.75","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:07.759","logLevel":"INFO","message":"Executing step BASIC: GIVEN the HTTP Mock Server <<httpMockServer = file:Sample Mock Server.http.mock.server.yaml>> with the Mock Request <<httpMock = file:heroes/Hero Squad.http.stub.yaml>>"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.797 [main] INFO wiremock.org.eclipse.jetty.util.log - Logging initialized @6307ms"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.893 [main] INFO wiremock.org.eclipse.jetty.server.Server - jetty-9.2.z-SNAPSHOT"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.912 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@772bc4c9{/__admin,null,AVAILABLE}"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.913 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@1ac2829e{/,null,AVAILABLE}"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.926 [main] INFO wiremock.org.eclipse.jetty.server.NetworkTrafficServerConnector - Started NetworkTrafficServerConnector@7fe0ca60{HTTP/1.1}{0.0.0.0:12121}"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":"11:13:09.927 [main] INFO wiremock.org.eclipse.jetty.server.Server - Started @6438ms"},{"time":"2018-12-27T11:13:09.966","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:09.969","logLevel":"INFO","message":"Finished executing step BASIC: GIVEN the HTTP Mock Server <<httpMockServer = file:Sample Mock Server.http.mock.server.yaml>> with the Mock Request <<httpMock = file:heroes/Hero Squad.http.stub.yaml>>"},{"time":"2018-12-27T11:13:09.974","logLevel":"INFO","message":"Executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},{"time":"2018-12-27T11:13:10.053","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heros\n\n]"},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":"11:13:10.202 [qtp2127092492-20] INFO / - RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'"},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\n  \"squadName\" : \"Super hero squad\",\n  \"homeTown\" : \"Metro City\",\n  \"formed\" : 2016,\n  \"secretBase\" : \"Super tower\",\n  \"active\" : true,\n  \"members\" : [ {\n    \"name\" : \"Molecule Man\",\n    \"age\" : 29,\n    \"secretIdentity\" : \"Dan Jukes\",\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\n  }, {\n    \"name\" : \"Madame Uppercut\",\n    \"age\" : 39,\n    \"secretIdentity\" : \"Jane Wilson\",\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\n  }, {\n    \"name\" : \"Eternal Flame\",\n    \"age\" : 1000000,\n    \"secretIdentity\" : \"Unknown\",\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\n  } ]\n}\n]"},{"time":"2018-12-27T11:13:10.628","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T11:13:10.629","logLevel":"INFO","message":"Finished executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},{"time":"2018-12-27T11:13:10.63","logLevel":"INFO","message":"Executing step BASIC: THEN I expect <<httpResponseVerify = {\"expectedStatusCode\":200,\"expectedBody\":{\"httpBodyVerifyMatchingType\":\"JSON_VERIFY\",\"httpBodyType\":\"JSON\",\"bodyVerify\":\"{\\\"=compareMode\\\": \\\"exact\\\",\\r\\n  \\\"squadName\\\": \\\"Super hero squad\\\",\\r\\n  \\\"homeTown\\\": \\\"Metro City\\\",\\r\\n  \\\"formed\\\": 2016,\\r\\n  \\\"secretBase\\\": \\\"Super tower\\\",\\r\\n  \\\"active\\\": true,\\r\\n  \\\"members\\\": [\\\"=compareMode: contains\\\",\\r\\n    {\\r\\n      \\\"name\\\": \\\"Eternal Flame\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Immortality\\\"\\r\\n      ]\\r\\n    }\\r\\n  ]\\r\\n}\"}}>> HTTP Response"},{"time":"2018-12-27T11:13:10.779","logLevel":"INFO","message":"Finished executing step BASIC: THEN I expect <<httpResponseVerify = {\"expectedStatusCode\":200,\"expectedBody\":{\"httpBodyVerifyMatchingType\":\"JSON_VERIFY\",\"httpBodyType\":\"JSON\",\"bodyVerify\":\"{\\\"=compareMode\\\": \\\"exact\\\",\\r\\n  \\\"squadName\\\": \\\"Super hero squad\\\",\\r\\n  \\\"homeTown\\\": \\\"Metro City\\\",\\r\\n  \\\"formed\\\": 2016,\\r\\n  \\\"secretBase\\\": \\\"Super tower\\\",\\r\\n  \\\"active\\\": true,\\r\\n  \\\"members\\\": [\\\"=compareMode: contains\\\",\\r\\n    {\\r\\n      \\\"name\\\": \\\"Eternal Flame\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Immortality\\\"\\r\\n      ]\\r\\n    }\\r\\n  ]\\r\\n}\"}}>> HTTP Response"},{"time":"2018-12-27T11:13:10.78","logLevel":"INFO","message":"11:13:10.779 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T11:13:10.78","logLevel":"INFO","message":"11:13:10.779 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T11:13:10.78","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:10.781","logLevel":"INFO","message":"Finished executing test \"Eternal Flame\" is immortal"},{"time":"2018-12-27T11:13:10.784","logLevel":"INFO","message":"Executing test Verify full squad"},{"time":"2018-12-27T11:13:10.785","logLevel":"INFO","message":"11:13:10.784 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T11:13:10.785","logLevel":"INFO","message":"11:13:10.785 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T11:13:10.785","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:10.787","logLevel":"INFO","message":"Executing step BASIC: GIVEN the HTTP Mock Server <<httpMockServer = file:Sample Mock Server.http.mock.server.yaml>> with the Mock Request <<httpMock = file:heroes/Hero Squad.http.stub.yaml>>"},{"time":"2018-12-27T11:13:10.915","logLevel":"INFO","message":"Finished executing step BASIC: GIVEN the HTTP Mock Server <<httpMockServer = file:Sample Mock Server.http.mock.server.yaml>> with the Mock Request <<httpMock = file:heroes/Hero Squad.http.stub.yaml>>"},{"time":"2018-12-27T11:13:10.916","logLevel":"INFO","message":"Executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},{"time":"2018-12-27T11:13:10.969","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heros\n\n]"},{"time":"2018-12-27T11:13:10.981","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\n  \"squadName\" : \"Super hero squad\",\n  \"homeTown\" : \"Metro City\",\n  \"formed\" : 2016,\n  \"secretBase\" : \"Super tower\",\n  \"active\" : true,\n  \"members\" : [ {\n    \"name\" : \"Molecule Man\",\n    \"age\" : 29,\n    \"secretIdentity\" : \"Dan Jukes\",\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\n  }, {\n    \"name\" : \"Madame Uppercut\",\n    \"age\" : 39,\n    \"secretIdentity\" : \"Jane Wilson\",\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\n  }, {\n    \"name\" : \"Eternal Flame\",\n    \"age\" : 1000000,\n    \"secretIdentity\" : \"Unknown\",\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\n  } ]\n}\n]"},{"time":"2018-12-27T11:13:10.982","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T11:13:10.982","logLevel":"INFO","message":"Finished executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},{"time":"2018-12-27T11:13:10.983","logLevel":"INFO","message":"Executing step BASIC: THEN I expect <<httpResponseVerify = {\"expectedStatusCode\":200,\"expectedBody\":{\"httpBodyVerifyMatchingType\":\"JSON_VERIFY\",\"httpBodyType\":\"JSON\",\"bodyVerify\":\"{\\r\\n  \\\"squadName\\\": \\\"Super hero squad\\\",\\r\\n  \\\"homeTown\\\": \\\"Metro City\\\",\\r\\n  \\\"formed\\\": 2016,\\r\\n  \\\"secretBase\\\": \\\"Super tower\\\",\\r\\n  \\\"active\\\": true,\\r\\n  \\\"members\\\": [\\r\\n    {\\r\\n      \\\"name\\\": \\\"Molecule Man\\\",\\r\\n      \\\"age\\\": 29,\\r\\n      \\\"secretIdentity\\\": \\\"Dan Jukes\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Radiation resistance\\\",\\r\\n        \\\"Turning tiny\\\",\\r\\n        \\\"Radiation blast\\\"\\r\\n      ]\\r\\n    },\\r\\n    {\\r\\n      \\\"name\\\": \\\"Madame Uppercut\\\",\\r\\n      \\\"age\\\": 39,\\r\\n      \\\"secretIdentity\\\": \\\"Jane Wilson\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Million tonne punch\\\",\\r\\n        \\\"Damage resistance\\\",\\r\\n        \\\"Superhuman reflexes\\\"\\r\\n      ]\\r\\n    },\\r\\n    {\\r\\n      \\\"name\\\": \\\"Eternal Flame\\\",\\r\\n      \\\"age\\\": 1000000,\\r\\n      \\\"secretIdentity\\\": \\\"Unknown\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Immortality\\\",\\r\\n        \\\"Heat Immunity\\\",\\r\\n        \\\"Inferno\\\",\\r\\n        \\\"Teleportation\\\",\\r\\n        \\\"Interdimensional travel\\\"\\r\\n      ]\\r\\n    }\\r\\n  ]\\r\\n}\"}}>> HTTP Response"},{"time":"2018-12-27T11:13:11.033","logLevel":"INFO","message":"Finished executing step BASIC: THEN I expect <<httpResponseVerify = {\"expectedStatusCode\":200,\"expectedBody\":{\"httpBodyVerifyMatchingType\":\"JSON_VERIFY\",\"httpBodyType\":\"JSON\",\"bodyVerify\":\"{\\r\\n  \\\"squadName\\\": \\\"Super hero squad\\\",\\r\\n  \\\"homeTown\\\": \\\"Metro City\\\",\\r\\n  \\\"formed\\\": 2016,\\r\\n  \\\"secretBase\\\": \\\"Super tower\\\",\\r\\n  \\\"active\\\": true,\\r\\n  \\\"members\\\": [\\r\\n    {\\r\\n      \\\"name\\\": \\\"Molecule Man\\\",\\r\\n      \\\"age\\\": 29,\\r\\n      \\\"secretIdentity\\\": \\\"Dan Jukes\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Radiation resistance\\\",\\r\\n        \\\"Turning tiny\\\",\\r\\n        \\\"Radiation blast\\\"\\r\\n      ]\\r\\n    },\\r\\n    {\\r\\n      \\\"name\\\": \\\"Madame Uppercut\\\",\\r\\n      \\\"age\\\": 39,\\r\\n      \\\"secretIdentity\\\": \\\"Jane Wilson\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Million tonne punch\\\",\\r\\n        \\\"Damage resistance\\\",\\r\\n        \\\"Superhuman reflexes\\\"\\r\\n      ]\\r\\n    },\\r\\n    {\\r\\n      \\\"name\\\": \\\"Eternal Flame\\\",\\r\\n      \\\"age\\\": 1000000,\\r\\n      \\\"secretIdentity\\\": \\\"Unknown\\\",\\r\\n      \\\"powers\\\": [\\r\\n        \\\"Immortality\\\",\\r\\n        \\\"Heat Immunity\\\",\\r\\n        \\\"Inferno\\\",\\r\\n        \\\"Teleportation\\\",\\r\\n        \\\"Interdimensional travel\\\"\\r\\n      ]\\r\\n    }\\r\\n  ]\\r\\n}\"}}>> HTTP Response"},{"time":"2018-12-27T11:13:11.034","logLevel":"INFO","message":"11:13:11.034 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T11:13:11.034","logLevel":"INFO","message":"11:13:11.034 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T11:13:11.034","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:11.035","logLevel":"INFO","message":"Finished executing test Verify full squad"},{"time":"2018-12-27T11:13:11.036","logLevel":"INFO","message":"Finished executing feature super hero squad"},]);