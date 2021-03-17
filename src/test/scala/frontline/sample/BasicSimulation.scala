/*
 * Copyright 2011-2018 GatlingCorp (https://gatling.io)
 *
 * All rights reserved.
 */

package frontline.sample

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseUrl("http://demo.testim.io")

  val scn = scenario("scenario1")
    .exec(http("Page 0").get("/"))

  setUp(
    scn.inject(
      nothingFor(5.seconds),
      rampConcurrentUsers(10).to(100).during(1.minutes)
      // constantUsersPerSec(1000).during(2.minutes)
    )
  ).protocols(httpConf)
}
