/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
 *
 * All rights reserved.
 */

package frontline.sample

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;



class BasicSimulation extends Simulation {
  val SERVICES_HOST = "https://services.testim.io";

  before() {
    val httpClient = HttpClientBuilder.create().build();
    val request = new HttpPost(this.SERVICES_HOST + "/executions/initialize");
    val json = "{\"projectId\":\"EFlU5RmSlcO9rhsKuMPd\",\"token\":\"yZZdfByGuQSlpT1oxBOBaVAkM7kQHUKlUNOvUZtjc5N5Kl421N\",\"branchName\":\"master\",\"lightweightMode\":true,\"localGrid\":true}";
    val entity = new StringEntity(json);
    request.setEntity(entity);
    val httpResponse = httpClient.execute(request)
    println("StatusCode - " + EntityUtils.toString(httpResponse.getEntity()))
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
