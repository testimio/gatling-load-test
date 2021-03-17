/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
 *
 * All rights reserved.
 */

package frontline.sample

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import java.util.UUID

import io.gatling.core.session.Expression



// import org.apache.http.client.HttpClient;
// import org.apache.http.impl.client.HttpClientBuilder;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.entity.StringEntity;
// import org.apache.http.util.EntityUtils;



class BasicSimulation extends Simulation {
  val SERVICES_HOST = "https://services.testim.io";
  val TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImNpOkVGbFU1Um1TbGNPOXJoc0t1TVBkIiwiaWF0IjoxNjE2MDE4MzkwLCJleHAiOjE2MTYwMjE5OTB9.DtP_bgDfPY6XaudNYLYCo8Pu7JMRmfKlVV7kMkDhink";

  // before() {
  //   val httpClient = HttpClientBuilder.create().build();
  //   val request = new HttpPost(this.SERVICES_HOST + "/executions/initialize");
  //   val json = "{\"projectId\":\"EFlU5RmSlcO9rhsKuMPd\",\"token\":\"yZZdfByGuQSlpT1oxBOBaVAkM7kQHUKlUNOvUZtjc5N5Kl421N\",\"branchName\":\"master\",\"lightweightMode\":true,\"localGrid\":true}";
  //   val entity = new StringEntity(json);
  //   request.setEntity(entity);
  //   val httpResponse = httpClient.execute(request)
  //   this.res = EntityUtils.toString(httpResponse.getEntity());
  // }

  //  val authAPI = exec(
  //   exec(
  //     http("POST Auth API")
  //       .post(this.SERVICES_HOST + "/executions/initialize")
  //       .body(ElFileBody("auth.json")).asJson
  //       .check(bodyString.saveAs("Auth_Response"))
  //       .check(status.is(200))
  //       .check(jsonPath("$.authData.token").find.saveAs("token")))
  //     exec{session => { tokenAPI = session("token").as[String]
  //     session}})

  val uuidfeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID().toString))


  val httpConf = http
    .baseUrl("https://services.testim.io")

 def  run() = {
    // exec { session => println("token print2"); session }
    // exec { session => println(tokenAPI:String); session }
    exec { session => session.set("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImNpOkVGbFU1Um1TbGNPOXJoc0t1TVBkIiwiaWF0IjoxNjE2MDE4MzkwLCJleHAiOjE2MTYwMjE5OTB9.DtP_bgDfPY6XaudNYLYCo8Pu7JMRmfKlVV7kMkDhink") }
    exec(http("lightweight")
      .post("/result/lightweight/test")
      .body(ElFileBody("result.json")).asJson
      .headers(Map("Authorization" -> "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImNpOkVGbFU1Um1TbGNPOXJoc0t1TVBkIiwiaWF0IjoxNjE2MDIyMTg3LCJleHAiOjE2MTYwMjU3ODd9.yZUwKB_fk5P5E7v-BQjpRgYVAapCEm57-pByBxWxG1k"))
      .check(status.in(200 to 210)))
      //.exec { session => println(session); session }

    //   .pause(1, 20)
  }



  val scn = scenario("scenario1")
    .pause(1)
    .feed(uuidfeeder)
    .exec(run())

  setUp(
    scn.inject(
      nothingFor(5.seconds),
      constantUsersPerSec(10).during(5.seconds)
    )
  ).protocols(httpConf)
}
