receiveModel([{"time":"2018-12-27T23:44:33.214","logLevel":"INFO","message":"Started executing test [\"Eternal Flame\" is immortal] at [unrelated/super hero squad/_Eternal Flame_ is immortal.test]"},{"time":"2018-12-27T23:44:34.811","logLevel":"INFO","message":"23:44:33.217 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T23:44:34.811","logLevel":"INFO","message":"23:44:34.810 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T23:44:34.811","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:34.818","logLevel":"INFO","message":"Started executing step [COMPOSED: GIVEN step 1]"},{"time":"2018-12-27T23:44:34.821","logLevel":"INFO","message":"Started executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:Hero Squad.http.stub.yaml>>]"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.231 [main] INFO wiremock.org.eclipse.jetty.util.log - Logging initialized @19927ms"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.431 [main] INFO wiremock.org.eclipse.jetty.server.Server - jetty-9.2.z-SNAPSHOT"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.465 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@1255de25{/__admin,null,AVAILABLE}"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.466 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@4fc3529{/,null,AVAILABLE}"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.491 [main] INFO wiremock.org.eclipse.jetty.server.NetworkTrafficServerConnector - Started NetworkTrafficServerConnector@5d58dc61{HTTP/1.1}{0.0.0.0:12121}"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"23:44:38.491 [main] INFO wiremock.org.eclipse.jetty.server.Server - Started @20190ms"},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:38.567","logLevel":"INFO","message":"Finished executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:Hero Squad.http.stub.yaml>>]; status: [PASSED]"},{"time":"2018-12-27T23:44:38.579","logLevel":"INFO","message":"Started executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]"},{"time":"2018-12-27T23:44:38.714","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heroes\n\n]"},{"time":"2018-12-27T23:44:39.747","logLevel":"INFO","message":"23:44:39.075 [qtp1266333611-25] INFO / - RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'"},{"time":"2018-12-27T23:44:39.747","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:39.747","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\r\n  \"squadName\" : \"Super hero squad\",\r\n  \"homeTown\" : \"Metro City\",\r\n  \"formed\" : 2016,\r\n  \"secretBase\" : \"Super tower\",\r\n  \"active\" : true,\r\n  \"members\" : [ {\r\n    \"name\" : \"Molecule Man\",\r\n    \"age\" : 29,\r\n    \"secretIdentity\" : \"Dan Jukes\",\r\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\r\n  }, {\r\n    \"name\" : \"Madame Uppercut\",\r\n    \"age\" : 39,\r\n    \"secretIdentity\" : \"Jane Wilson\",\r\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\r\n  }, {\r\n    \"name\" : \"Eternal Flame\",\r\n    \"age\" : 1000000,\r\n    \"secretIdentity\" : \"Unknown\",\r\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\r\n  } ]\r\n}\n]"},{"time":"2018-12-27T23:44:39.749","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T23:44:39.749","logLevel":"INFO","message":"Finished executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]; status: [PASSED]"},{"time":"2018-12-27T23:44:39.751","logLevel":"INFO","message":"Started executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]"},{"time":"2018-12-27T23:44:39.991","logLevel":"INFO","message":"Finished executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]; status: [PASSED]"},{"time":"2018-12-27T23:44:39.995","logLevel":"INFO","message":"Started executing step [COMPOSED: GIVEN step 2]"},{"time":"2018-12-27T23:44:39.997","logLevel":"INFO","message":"Started executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:Hero Squad.http.stub.yaml>>]"},{"time":"2018-12-27T23:44:40.19","logLevel":"INFO","message":"Finished executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:Hero Squad.http.stub.yaml>>]; status: [PASSED]"},{"time":"2018-12-27T23:44:40.194","logLevel":"INFO","message":"Started executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]"},{"time":"2018-12-27T23:44:40.309","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heroes\n\n]"},{"time":"2018-12-27T23:44:40.335","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\r\n  \"squadName\" : \"Super hero squad\",\r\n  \"homeTown\" : \"Metro City\",\r\n  \"formed\" : 2016,\r\n  \"secretBase\" : \"Super tower\",\r\n  \"active\" : true,\r\n  \"members\" : [ {\r\n    \"name\" : \"Molecule Man\",\r\n    \"age\" : 29,\r\n    \"secretIdentity\" : \"Dan Jukes\",\r\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\r\n  }, {\r\n    \"name\" : \"Madame Uppercut\",\r\n    \"age\" : 39,\r\n    \"secretIdentity\" : \"Jane Wilson\",\r\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\r\n  }, {\r\n    \"name\" : \"Eternal Flame\",\r\n    \"age\" : 1000000,\r\n    \"secretIdentity\" : \"Unknown\",\r\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\r\n  } ]\r\n}\n]"},{"time":"2018-12-27T23:44:40.336","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T23:44:40.337","logLevel":"INFO","message":"Finished executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]; status: [PASSED]"},{"time":"2018-12-27T23:44:40.341","logLevel":"INFO","message":"Started executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]"},{"time":"2018-12-27T23:44:40.452","logLevel":"INFO","message":"Finished executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]; status: [PASSED]"},{"time":"2018-12-27T23:44:40.454","logLevel":"INFO","message":"Finished executing step [COMPOSED: GIVEN step 2]; status: [PASSED]"},{"time":"2018-12-27T23:44:40.457","logLevel":"INFO","message":"Finished executing step [COMPOSED: GIVEN step 1]; status: [PASSED]"},{"time":"2018-12-27T23:44:40.46","logLevel":"INFO","message":"23:44:40.458 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T23:44:40.46","logLevel":"INFO","message":"23:44:40.459 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T23:44:40.46","logLevel":"INFO","message":""},{"time":"2018-12-27T23:44:40.46","logLevel":"INFO","message":"Finished executing test [\"Eternal Flame\" is immortal] at [unrelated/super hero squad/_Eternal Flame_ is immortal.test]; status: [PASSED]"},]);