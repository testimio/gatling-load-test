/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
 *
 * All rights reserved.
 */

package frontline.sample

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import org.apache.http.client._
import org.apache.http.client.methods._



class BasicSimulation extends Simulation {
  val SERVICES_HOST = "https://services.testim.io";

  before() {
    val httpClient = HttpClientBuilder.create.build
    HttpPost request = new HttpPost(this.SERVICES_HOST + "/executions/initialize");
    String json = "{\"projectId\":\"EFlU5RmSlcO9rhsKuMPd\",\"token\":\"yZZdfByGuQSlpT1oxBOBaVAkM7kQHUKlUNOvUZtjc5N5Kl421N\",\"branchName\":\"master\",\"lightweightMode\":true,\"localGrid\":true}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    val httpResponse = httpClient.execute(request)
    println("StatusCode - " + httpResponse.getStatusLine.getStatusCode)
    httpClient.close()
  }

  val httpConf = http
    .baseUrl("http://demo.testim.io")

  val scn = scenario("scenario1")
    .exec(http("Page 0").get("/bundle.js"))

  setUp(
    scn.inject(
      nothingFor(15.seconds),
      constantUsersPerSec(500).during(1.minutes)
    )
  ).protocols(httpConf)
}
