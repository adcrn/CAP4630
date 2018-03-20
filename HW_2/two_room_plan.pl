% University of Central Florida
% CAP 4630 - Spring 2018
% Authors: Nhi Nguyen and Alexander deCurnou

:- module( planner,
	   [
	       plan/4,change_state/3, conditions_met/2, member_state/2,
           move/3, go/2, test/0, test2/0
	   ]).

:- [utils].

% Compare the current state to the goal state
% If they are the same, then print the moves.
plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).

plan(State, Goal, Been_list, Moves) :-
				% Make a move and if it meets the conditions of the state
				% and the state has not been visited, then add it to the list of moves
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			% Plan next move using the new state and new list of visited states
			plan(Child_state, Goal, New_been_list, New_moves),!.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

move(pickup(X), 
        [handempty, clear(X), on(X, Y, Z), handroom(Z)],
		[del(handempty), del(clear(X)), del(on(X, Y, Z)), add(clear(Y)),	add(holding(X))]).

move(pickup(X), 
        [handempty, clear(X), ontable(X, Z), handroom(Z)],
		[del(handempty), del(clear(X)), del(ontable(X, Z)), add(holding(X))]).

move(putdown(X), 
        [holding(X), handroom(Z)],
		[del(holding(X)), add(ontable(X, Z)), add(clear(X)), add(handempty)]).

move(stack(X, Y), 
        [holding(X), clear(Y), handroom(Z)],
		[del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y, Z)), add(clear(X))]).

move(goroom1(), 
        [handroom(2)],
        [del(handroom(2)), add(handroom(1))]).

move(goroom2(),
        [handroom(1)],
        [del(handroom(1)), add(handroom(2))]).

/* run commands */
go(S, G) :- plan(S, G, [S], []).

test1 :- go([handempty, ontable(b,1), ontable(c, 1), on(a, b, 1), clear(c), clear(a), handroom(1)],
            [handempty, ontable(c, 2), on(a, b, 2), on(b, c, 2), clear(a),  handroom(1)]).

test :- go([handempty, ontable(b,1), on(a, b, 1), clear(a), handroom(1)],
            [handempty, ontable(b,2), on(a, b, 2), clear(a), handroom(1)]).
