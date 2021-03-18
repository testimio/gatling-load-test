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
    .baseUrl("https://services.testim.io")


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
    // exec{session => println("authToken print2")} 
    // exec { session => session.set("authToken", tokenAPI)}
    // exec { session => println(session("authToken")); session }
    exec { session => session.set("authToken", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImNpOkVGbFU1Um1TbGNPOXJoc0t1TVBkIiwiaWF0IjoxNjE2MDE4MzkwLCJleHAiOjE2MTYwMjE5OTB9.DtP_bgDfPY6XaudNYLYCo8Pu7JMRmfKlVV7kMkDhink") }
    .exec(http("lightweight")
      .post("/result/lightweight/test")
      .body(ElFileBody("result.json")).asJson
      .headers(sessionHeaders)
      .check(status.in(200 to 210)))
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
          constantUsersPerSec(5).during(20.seconds)
        )
      )
  ).protocols(httpConf)
}
