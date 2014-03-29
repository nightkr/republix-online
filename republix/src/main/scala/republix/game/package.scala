/*
 * Copyright © 2014 Teo Klestrup, Carl Dybdahl
 *
 * This file is part of Republix.
 *
 * Republix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Republix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Republix.  If not, see <http://www.gnu.org/licenses/>.
 */

package republix

import republix.sim._
import republix.io._

package object game {

	import shapeless._
	// client -> game
	sealed trait Command
	sealed trait PhaseCommand extends Command
	case class Intro(party: String) extends Command
	case class SendChat(chat: String) extends Command
	case class SetReady(ready: Boolean) extends PhaseCommand
	case class ProposeAmendment(law: GameNode, update: Option[Intensity]) extends PhaseCommand

	// game -> client
	sealed trait Update
	sealed trait GenericUpdate extends Update
	sealed trait PhaseUpdate extends Update
	case class YouAre(party: Party) extends GenericUpdate
	case class IntroModel(model: GameModel) extends GenericUpdate
	case class Chat(chat: String) extends GenericUpdate
	case class SwitchPhase(newPhase: GamePhase, state: GameState) extends GenericUpdate
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

	import Serial._
	implicit val serialIntensity: Serial[Intensity] =
		serialInstance.project(doubleSerial,
			(x: Intensity) => x.intensity,
			Intensity.apply _)
	implicit val serialOptionIntensity: Serial[Option[Intensity]] = Serial.optionSerial(serialIntensity)
	implicit val serialNode: Serial[GameNode] = TypeClass[Serial, GameNode]
	implicit val serialLink: Serial[Link] = TypeClass[Serial, Link]
	implicit val serialState: Serial[GameState] = TypeClass[Serial, GameState]
	implicit val serialModel: Serial[GameModel] = TypeClass[Serial, GameModel]
	implicit val serialParty: Serial[Party] = TypeClass[Serial, Party]
	implicit val serialPhase: Serial[GamePhase] = TypeClass[Serial, GamePhase]
	implicit val serialCommand: Serial[Command] = TypeClass[Serial, Command]
	implicit val serialUpdate: Serial[Update] = TypeClass[Serial, Update]

}