package republix

import republix.sim._
import republix.io._

package object game {

	import shapeless._
	// client -> game
	sealed trait Command
	case class Intro(party: String) extends Command
	case class SendChat(chat: String) extends Command

	// game -> client
	sealed trait Update
	sealed trait GenericUpdate extends Update
	sealed trait PhaseUpdate extends Update
	case class YouAre(party: Party) extends GenericUpdate
	case class IntroModel(todo: String) extends GenericUpdate
	case class Chat(chat: String) extends GenericUpdate
	case class SwitchPhase(newPhase: GamePhase) extends GenericUpdate
	case class NewParty(party: Party) extends PhaseUpdate

	// case classes because it works better with shapeless
	sealed trait GamePhase
	case class NewsPhase() extends GamePhase
	case class LobbyPhase() extends GamePhase
	case class LawsPhase() extends GamePhase
	case class VotePhase() extends GamePhase
	case class ElectionPhase() extends GamePhase

	// todo: use id along with name
	case class Party(name: String)

	implicit val serialParty: Serial[Party] = TypeClass[Serial, Party]
	implicit val serialPhase: Serial[GamePhase] = TypeClass[Serial, GamePhase]
	implicit val serialCommand: Serial[Command] = TypeClass[Serial, Command]
	implicit val serialUpdate: Serial[Update] = TypeClass[Serial, Update]

}