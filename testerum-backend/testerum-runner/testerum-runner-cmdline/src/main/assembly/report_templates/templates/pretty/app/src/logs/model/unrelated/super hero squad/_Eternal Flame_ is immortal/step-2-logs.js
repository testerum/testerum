receiveModel([{"time":"2018-12-27T11:13:09.974","logLevel":"INFO","message":"Executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},{"time":"2018-12-27T11:13:10.053","logLevel":"INFO","message":"HTTP Request [\nGET http://localhost:12121/heros\n\n]"},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":"11:13:10.202 [qtp2127092492-20] INFO / - RequestHandlerClass from context returned com.github.tomakehurst.wiremock.http.StubRequestHandler. Normalized mapped under returned 'null'"},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":""},{"time":"2018-12-27T11:13:10.627","logLevel":"INFO","message":"HTTP Response [\nHTTP/1.1 200\nContent-Type: application/json\nVary: Accept-Encoding, User-Agent\nTransfer-Encoding: chunked\nServer: Jetty(9.2.z-SNAPSHOT)\n\n{\n  \"squadName\" : \"Super hero squad\",\n  \"homeTown\" : \"Metro City\",\n  \"formed\" : 2016,\n  \"secretBase\" : \"Super tower\",\n  \"active\" : true,\n  \"members\" : [ {\n    \"name\" : \"Molecule Man\",\n    \"age\" : 29,\n    \"secretIdentity\" : \"Dan Jukes\",\n    \"powers\" : [ \"Radiation resistance\", \"Turning tiny\", \"Radiation blast\" ]\n  }, {\n    \"name\" : \"Madame Uppercut\",\n    \"age\" : 39,\n    \"secretIdentity\" : \"Jane Wilson\",\n    \"powers\" : [ \"Million tonne punch\", \"Damage resistance\", \"Superhuman reflexes\" ]\n  }, {\n    \"name\" : \"Eternal Flame\",\n    \"age\" : 1000000,\n    \"secretIdentity\" : \"Unknown\",\n    \"powers\" : [ \"Immortality\", \"Heat Immunity\", \"Inferno\", \"Teleportation\", \"Interdimensional travel\" ]\n  } ]\n}\n]"},{"time":"2018-12-27T11:13:10.628","logLevel":"INFO","message":"Http Request executed successfully"},{"time":"2018-12-27T11:13:10.629","logLevel":"INFO","message":"Finished executing step BASIC: WHEN I execute <<httpRequest = {\"method\":\"GET\",\"url\":\"http://localhost:12121/heros\",\"headers\":{}}>> HTTP Request"},]);