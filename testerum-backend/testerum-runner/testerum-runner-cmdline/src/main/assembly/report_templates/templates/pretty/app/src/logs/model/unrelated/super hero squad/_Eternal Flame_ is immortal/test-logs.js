receiveModel([{"time":"2018-12-27T18:09:42.643","logLevel":"INFO","message":"Started executing test [\"Eternal Flame\" is immortal] at [unrelated/super hero squad/_Eternal Flame_ is immortal.test]"},{"time":"2018-12-27T18:09:43.203","logLevel":"INFO","message":"18:09:42.645 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T18:09:43.203","logLevel":"INFO","message":"18:09:43.203 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=BEFORE_EACH_TEST, className=http.mock.HttpMockSteps, methodName=beforeTest, order=1000000, description=null)"},{"time":"2018-12-27T18:09:43.203","logLevel":"INFO","message":""},{"time":"2018-12-27T18:09:43.206","logLevel":"INFO","message":"Started executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:heroes/Hero Squad.http.stub.yaml>>]"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.845 [main] INFO wiremock.org.eclipse.jetty.util.log - Logging initialized @4869ms"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.921 [main] INFO wiremock.org.eclipse.jetty.server.Server - jetty-9.2.z-SNAPSHOT"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.938 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@3ab70d34{/__admin,null,AVAILABLE}"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.938 [main] INFO wiremock.org.eclipse.jetty.server.handler.ContextHandler - Started w.o.e.j.s.ServletContextHandler@7af0693b{/,null,AVAILABLE}"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.950 [main] INFO wiremock.org.eclipse.jetty.server.NetworkTrafficServerConnector - Started NetworkTrafficServerConnector@37f627d0{HTTP/1.1}{0.0.0.0:12121}"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"18:09:44.950 [main] INFO wiremock.org.eclipse.jetty.server.Server - Started @4975ms"},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":""},{"time":"2018-12-27T18:09:44.981","logLevel":"INFO","message":"Finished executing step [BASIC: GIVEN the HTTP Mock Server <<httpMockServer=file:Sample Mock Server.http.mock.server...>> with the Mock Request <<httpMock=file:heroes/Hero Squad.http.stub.yaml>>]; status: [PASSED]"},{"time":"2018-12-27T18:09:44.987","logLevel":"INFO","message":"Started executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]"},{"time":"2018-12-27T18:09:45.053","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heros\n\n]"},{"time":"2018-12-27T18:09:45.577","logLevel":"INFO","message":"18:09:45.186 [qtp848019559-20] INFO / - RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'"},{"time":"2018-12-27T18:09:45.577","logLevel":"INFO","message":""},{"time":"2018-12-27T18:09:45.577","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\n  \"squadName\" : \"Super hero squad\",\n  \"homeTown\" : \"Metro City\",\n  \"formed\" : 2016,\n  \"secretBase\" : \"Super tower\",\n  \"active\" : true,\n  \"members\" : [ {\n    \"name\" : \"Molecule Man\",\n    \"age\" : 29,\n    \"secretIdentity\" : \"Dan Jukes\",\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\n  }, {\n    \"name\" : \"Madame Uppercut\",\n    \"age\" : 39,\n    \"secretIdentity\" : \"Jane Wilson\",\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\n  }, {\n    \"name\" : \"Eternal Flame\",\n    \"age\" : 1000000,\n    \"secretIdentity\" : \"Unknown\",\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\n  } ]\n}\n]"},{"time":"2018-12-27T18:09:45.579","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T18:09:45.579","logLevel":"INFO","message":"Finished executing step [BASIC: WHEN I execute <<httpRequest={\"method\":\"GET\",\"url\":\"http://localhost:...>> HTTP Request]; status: [PASSED]"},{"time":"2018-12-27T18:09:45.58","logLevel":"INFO","message":"Started executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]"},{"time":"2018-12-27T18:09:45.719","logLevel":"INFO","message":"Finished executing step [BASIC: THEN I expect <<httpResponseVerify={\"expectedStatusCode\":200,\"expectedBody\"...>> HTTP Response]; status: [PASSED]"},{"time":"2018-12-27T18:09:45.721","logLevel":"INFO","message":"18:09:45.720 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - start executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T18:09:45.721","logLevel":"INFO","message":"18:09:45.720 [main] INFO com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook - done executing hook HookDef(phase=AFTER_EACH_TEST, className=selenium.actions.hooks.WebDriverShutdownHook, methodName=destroyWebDriver, order=2147483647, description=null)"},{"time":"2018-12-27T18:09:45.721","logLevel":"INFO","message":""},{"time":"2018-12-27T18:09:45.721","logLevel":"INFO","message":"Finished executing test [\"Eternal Flame\" is immortal] at [unrelated/super hero squad/_Eternal Flame_ is immortal.test]; status: [PASSED]"},]);