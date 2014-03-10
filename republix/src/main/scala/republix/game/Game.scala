package republix.game

import republix.io._
import republix.sim._

class Game(clients: In[(In[Command], Out[Update])]) {

	object model extends Model {
		type Node = String
		val links: Map[(Node, Node), Link] = Map()
		val serialized = IntroModel("YAY!")
	}

	// todo: somehow freeze once games starts
	var players: Map[Party, Out[Update]] = Map()

	import java.util.concurrent.{Executors, ExecutorService}
	val pool: ExecutorService = Executors.newFixedThreadPool(1)
	def atomically(body: => Unit): Unit = {
		pool.execute(new Runnable {
			def run() = body
		})
	}

	clients.listen { newPlayer =>
		println("Player attempting to connect.")
		newPlayer._1.setReceive {
			case Intro(partyName) =>
				println(s"Intro received. $partyName connecting.")
				atomically {
					val party = Party(partyName)
					players.foreach { other =>
						other._2.send(NewParty(party))
						newPlayer._2.send(NewParty(other._1))
					}
					players += party -> newPlayer._2
					newPlayer._2.send(model.serialized)
					newPlayer._2.send(YouAre(party))
					newPlayer._2.send(SwitchPhase(LobbyPhase()))
					newPlayer._1.listen(playerListener(party))
				}
			case _ =>
				println("Player is breaking protocol. Kicking.")
				newPlayer._2.close
		}
	}

	def playerListener(party: Party): Command => Unit = (message: Command) => message match {
		case SendChat(chat: String) =>
			players.foreach { player =>
				player._2.send(Chat(party.name + ": " + chat))
			}
		case _ =>
			players(party).close
	}

}