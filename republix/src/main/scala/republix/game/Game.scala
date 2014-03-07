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
	case class NewParty(party: String) extends Update
	case class IntroModel(todo: String) extends Update
	case class Chat(chat: String) extends Update

	// todo: find a better place for these
	implicit val serialCommand: Serial[Command] = TypeClass[Serial, Command]
	implicit val serialUpdate: Serial[Update] = TypeClass[Serial, Update]

	class Game(clients: In[(In[Command], Out[Update])]) {

		// todo: use an id instead of name
		case class Party(name: String)

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
				case Intro(party) =>
					println("Intro received. $party connecting.")
					atomically {
						players.foreach { other =>
							other._2.send(NewParty(party))
							newPlayer._2.send(NewParty(other._1.name))
						}
						players += Party(party) -> newPlayer._2
						newPlayer._2.send(model.serialized)
						newPlayer._1.listen(playerListener(Party(party)))
					}
				case _ =>
					println("$party is breaking protocol. Kicking.")
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

}