/*
 * Copyright 2019 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lorislab.p6.integration.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.lorislab.p6.test.IntegrationTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Slf4j
@IntegrationTest
@Testcontainers
public class StartProcessTest {


    @Container
    private static final DockerComposeContainer ENVIRONMENT = new DockerComposeContainer(new File("docker-compose-test.yml"))
            .withPull(false)
            .withTailChildContainers(true)
            .withLogConsumer("p6", new ToStringConsumer())
            .withExposedService("p6", 8080, Wait.forHttp("/p6/runtime/process/count").forResponsePredicate(s -> s.equals("3")));

    @Test
    public void startProcess() throws Exception {
        RestAssured.baseURI = "http://localhost:8080/p6";

        String request = "{\n" +
                "    \"parameter\": \"value\",\n" +
                "    \"parameter2\": 2,\n" +
                "    \"data\": {\n" +
                "        \"key1\": \"value1\",\n" +
                "        \"key2\": \"value2\"\n" +
                "    }\n" +
                "}";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(request)
                .post("/process/start/{processId}", "org.lorislab.p6.example.Test2");
        log.info("Start process instance: {}", response.asString());
    }
}
