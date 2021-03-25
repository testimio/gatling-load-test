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

  val uuidFeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID().toString))
  val countFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val httpConf = http
    .baseUrl("https://services.testim.io")
    // .baseUrl("https://loadtest.testim.io")
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
    .exec(http("lightweight")
      // .post("/result/lightweight/test")
      // .body(ElFileBody("result.json")).asJson
      // .headers(sessionHeaders)
      .get("/health")
      .queryParam("reqId", "${uuid}${userId}")
      .check(status.in(200 to 210)))
    .pause(100.milliseconds)
 )

  val authScenario = scenario("auth")
    .exec(authAPI)

  val load = scenario("load")
    .pause(1)
    .feed(uuidFeeder)
    .feed(countFeeder)
    .exec(run)

  setUp(
    authScenario.inject(atOnceUsers(1)).noShard
      .andThen(
        load.inject(
          nothingFor(5.seconds),
          rampUsersPerSec(10).to(30000).during(30.minutes),
          constantUsersPerSec(30000).during(3.minutes),
          nothingFor(15.seconds),
        )
      )
  ).protocols(httpConf)
}
