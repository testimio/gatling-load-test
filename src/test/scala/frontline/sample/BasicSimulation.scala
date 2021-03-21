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


class BasicSimulation extends Simulation {
  private var tokenAPI = "";

  val uuidfeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID().toString))

  val httpConf = http
    // .baseUrl("https://services.testim.io")
    // .baseUrl("https://demo.testim.io")
    .baseUrl("https://a706ffca1c6c011eaa89006696688415-127946467.us-west-2.elb.amazonaws.com")
    .shareConnections


  val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}")
  
  val authAPI = exec(
    exec(
      http("POST Auth API")
        .post("/executions/initialize")
        .body(ElFileBody("auth.json")).asJson
        .check(bodyString.saveAs("Auth_Response"))
        .check(status.is(200))
        .check(jsonPath("$.authData.token").exists.saveAs("authToken")))
      exec{session => { tokenAPI = session("authToken").as[String]
      session}}
    )



 val run = exec(
    exec { session => session.set("authToken", tokenAPI)}
    .exec(http("healthz")
      // .post("/result/lightweight/test")
      // .body(ElFileBody("result.json")).asJson
      // .headers(sessionHeaders)
      .get("/healthz")
      .check(status.in(200 to 210)))
    .pause(100.milliseconds)
 )

  val authScenario = scenario("auth")
    .exec(authAPI)

  val load = scenario("load")
    .pause(1)
    .feed(uuidfeeder)
    .exec(run)

  setUp(
    authScenario.inject(atOnceUsers(1)).noShard
      .andThen(
        load.inject(
          nothingFor(5.seconds),
          constantUsersPerSec(20000).during(30.seconds),
          nothingFor(15.seconds),
        )
      )
  ).protocols(httpConf)
}
