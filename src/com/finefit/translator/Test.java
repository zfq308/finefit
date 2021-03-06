/* Copyright 2014 David Faitelson

This file is part of FineFit.

FineFit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FineFit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FineFit. If not, see <http://www.gnu.org/licenses/>.

*/


package com.finefit.translator;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
			Sig[] user = new Sig[1];
			user[0] = new Sig("User", 3);
			user[0].print(System.out);

			Map<String, String> stateVariables = new HashMap<String, String>();
			stateVariables.put("loggedin", "set User");
			stateVariables.put("registered", "set User");
			stateVariables.put("existing", "set User");

			State state = new State(stateVariables);
			state.print(" u in loggedin.User - User.registered & User\n", "s", System.out);

			List<String> preds = new ArrayList<String>();
			preds.add("loggedin in registered");
			Invariant invariant = new Invariant(preds);
			invariant.print(state, System.out);

			String guard = "u !in loggedin";
			String[] exprs = new String[3];
			exprs[0] = "loggedin + u";
			exprs[1] = "registered";
			exprs[2] = "OK";
			String[] frame = new String[3];
			frame[0] = "loggedin";
			frame[1] = "registered";
			frame[2] = "report";

			GuardedExpr[] expr = new GuardedExpr[2];
			expr[0] = new GuardedExpr(guard, exprs);
			expr[0].print(frame, state, System.out);

			String guard2 = " #registered < 3";
			String[] exprs2 = new String[3];
			exprs2[0] = "loggedin";
			exprs2[1] = "registered + u";
			exprs2[2] = "OK";
		
			expr[1] = new GuardedExpr(guard2, exprs2);

			String params = "u: User, c:User";

			Operation[] operation = new Operation[1];
			operation[0] = new Operation("register", params, frame, expr);
			operation[0].print(state, System.out);

			Spec spec = new Spec(state, invariant, user, operation, new Enumeration[0]);
			spec.print(System.out);
	}

}
