/*
 * Copyright (c) 2011 Miguel Ceriani
 * miguel.ceriani@gmail.com

 * This file is part of Semantic Web Open datatafloW System (SWOWS).

 * SWOWS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * SWOWS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General
 * Public License along with SWOWS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.swows.graph.events;

public class Transaction {
	
	static private int transactionCount = 0;
	
	private int id;
//	private boolean open = true;
	
	private Transaction(int id) {
		this.id = id;
	}
	
	public static synchronized Transaction newTransaction() {
		return new Transaction(transactionCount++);
	}
	
	public boolean equals(Object obj) {
		if (! (obj instanceof Transaction) )
			return false;
		return ((Transaction) obj).id == id;
	}
	
}
