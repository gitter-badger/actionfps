package acleague.ingesters

case class DemoRecorded(dateTime: String, mode: String, map: String, size: String)
object DemoRecorded {
  val capture = """Demo "(.*):\s+(.*), ([^\s]+), (\d+[^\s]+), .*" recorded\.""".r
  def unapply(input: String): Option[DemoRecorded] =
    for { capture(dateTime, mode, map, size) <- Option(input) }
    yield DemoRecorded(dateTime, mode, map, size)
}

case class DemoWritten(filename: String, size: String)
object DemoWritten {
  val capture = """demo written to file "([^"]+)" \(([^\)]+)\)""".r
  def unapply(input: String): Option[DemoWritten] =
    for { capture(filename, size) <- Option(input) }
    yield DemoWritten(filename, size)
}

case class GameFinishedHeader(mode: GameMode.GameMode, map: String, state: String)
object GameFinishedHeader {
  val capture = """Game status:\s+(.*)\s+on\s+([^\s]+), game finished, ([^\s]+), \d+ clients""".r
  def unapply(input: String): Option[GameFinishedHeader] =
    for {
      capture(mode, map, state) <- Option(input)
      foundMode <- GameMode.gamemodes.find(_.name == mode)
    } yield GameFinishedHeader(foundMode, map, state)
}
object VerifyTableHeader {
  def unapply(input: String): Boolean = {

    val capture = """cn\s+name\s+.*""".r
    input match {
      case capture() => true
      case _ => false
    }
  }
}
object TeamModes {

  object FragStyle {

    case class IndividualScore(cn: Int, name: String, team: String, score: Int, frag: Int, death: Int, tk: Int, ping: Int, role: String, host: String) extends CreatesGenericIndividualScore {
      override def project: GenericIndividualScore = GenericIndividualScore(name, team, None, score, frag, Option(host))
    }

    object IndividualScore {
      val capture = """\s?(\d+)\s([^\s]+)\s+([^\s]+)\s+([^\s]+)\s+(-?\d+)\s+(\d+)\s+(\d+)\s+(-?\d+)\s+([^\s]+)\s+([^\s]+)\s*""".r
      def unapply(input: String): Option[IndividualScore] =
        for { capture(cn, name, team, score, frag, death, tk, ping, role, host) <- Option(input) }
        yield IndividualScore(cn.toInt, name, team, score.toInt, frag.toInt, death.toInt, tk.toInt, ping.toInt, role, host)
    }

    case class IndividualScoreDisconnected(name: String, team: String, score: Int, frag: Int) extends CreatesGenericIndividualScore {
      override def project: GenericIndividualScore = GenericIndividualScore(name, team, None, score, frag, None)
    }

    object IndividualScoreDisconnected {
      def unapply(input: String): Option[IndividualScoreDisconnected] = {
        val capture = """\s+([^\s]+)\s+(RVSF|CLA)\s+(-?\d+)\s+(-?\d+)\s+\-\s+\-\s+disconnected""".r
        for { capture(name, team, flag, score, frag) <- Option(input) }
        yield IndividualScoreDisconnected(name, team, score.toInt, frag.toInt)
      }
    }
    case class TeamScore(teamName: String, players: Int, frags: Int)  extends CreatesGenericTeamScore {
      override def project: GenericTeamScore = GenericTeamScore(teamName, players, None, frags)
    }

    object TeamScore {
      val capture = """Team\s+([^\s]+):\s+(\d+)\s+players,\s+(-?\d+)\s+frags.*""".r
      def unapply(input: String): Option[TeamScore] =
        for { capture(teamName, players, frags) <- Option(input) }
        yield TeamScore(teamName, players.toInt, frags.toInt)
    }

  }

  case class GenericTeamScore(name: String, players: Int, flags: Option[Int], frags: Int)

  trait CreatesGenericTeamScore {
    def project: GenericTeamScore
  }

  case class GenericIndividualScore(name: String, team: String, flag: Option[Int], score: Int, frag: Int, host: Option[String])
  trait CreatesGenericIndividualScore {
    def project: GenericIndividualScore
  }
  object FlagStyle {
    case class IndividualScore(cn: Int, name: String, team: String, flag: Int, score: Int, frag: Int, death: Int, tk: Int, ping: Int, role: String, host: String) extends CreatesGenericIndividualScore {
      def project = GenericIndividualScore(name, team, Option(flag), score, frag, Option(host))
    }

    object IndividualScore {
      val capture = """\s?(\d+)\s([^\s]+)\s+([^\s]+)\s+(\d+)\s+(-?\d+)\s+(-?\d+)\s+(\d+)\s+(\d+)\s+(-?\d+)\s+([^\s]+)\s+([^\s]+)\s*""".r
      def unapply(input: String): Option[IndividualScore] = {
        for { capture(cn, name, team, flag, score, frag, death, tk, ping, role, host) <- Option(input) }
        yield IndividualScore(cn.toInt, name, team, flag.toInt, score.toInt, frag.toInt, death.toInt, tk.toInt, ping.toInt, role, host)
      }
    }

    case class IndividualScoreDisconnected(name: String, team: String, flag: Int, score: Int, frag: Int)  extends CreatesGenericIndividualScore {
      override def project: GenericIndividualScore = GenericIndividualScore(name, team, Option(flag), score, frag, None)
    }

    object IndividualScoreDisconnected {
      val capture = """\s+([^\s]+)\s+(RVSF|CLA)\s+(\d+)\s+(-?\d+)\s+(-?\d+)\s+\-\s+\-\s+disconnected""".r
      def unapply(input: String): Option[IndividualScoreDisconnected] = {
        for { capture(name, team, flag, score, frag) <- Option(input) }
        yield IndividualScoreDisconnected(name, team, flag.toInt, score.toInt, frag.toInt)
      }
    }

    case class TeamScore(name: String, players: Int, frags: Int, flags: Int) extends CreatesGenericTeamScore {
      override def project: GenericTeamScore = GenericTeamScore(name, players, Option(flags), frags)
    }

    object TeamScore {
      val capture = """Team\s+([^\s]+):\s+(\d+)\s+players,\s+(-?\d+)\s+frags,\s+(\d+)\s+flags""".r
      def unapply(input: String): Option[TeamScore] =
        for { capture(name, players, frags, flags) <- Option(input) }
        yield TeamScore(name, players.toInt, frags.toInt, flags.toInt)
    }

  }

}