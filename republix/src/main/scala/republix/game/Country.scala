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

package republix.game

import republix.sim._

case class Country(
	name: String,
	model: GameModel,
	startingState: GameState
)

object Country {

	val TestCountry = Country("The Happy People's Democratic Republic of Hell",
		GameModel(
			Set(
				GameNode("Income Taxes", true), GameNode("Air Taxes", true), GameNode("Tax Taxes", true),
				GameNode("Health", false), GameNode("Crime", false)),
			Map()),
		GameState(
			Map(
				GameNode("Income Taxes", true) -> Intensity(0.99), GameNode("Air Taxes", true) -> Intensity(0.5),
				GameNode("Tax Taxes", true) -> Intensity(0.99),
				GameNode("Health", false) -> Intensity(0.01), GameNode("Crime", false) -> Intensity(0.99))))

	// todo: write country IO stuff to load and let users choose countries

}