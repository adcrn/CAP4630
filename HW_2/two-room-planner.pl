% University of Central Florida
% CAP 4630 - Spring 2018
% Authors: Nhi Nguyen and Alexander deCurnou

:- module( planner,
	   [
	       plan/4,change_state/3,conditions_met/2,member_state/2,
	       move/3,go/2,test1/0,test2/0, test3/0, test4/0, test5/0
	   ]).

:- [utils].

plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
plan(State, Goal, Been_list, Moves) :-
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			plan(Child_state, Goal, New_been_list, New_moves),!.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

/* move types */

move(pickup(X), [oneroom, handempty, clear(X), on(X, Y)],
		[del(handempty), del(clear(X)), del(on(X, Y)),
				 add(clear(Y)),	add(holding(X))]).

move(pickup(X), [oneroom, handempty, clear(X), ontable(X)],
		[del(handempty), del(clear(X)), del(ontable(X)),
				 add(holding(X))]).

move(putdown(X), [oneroom, holding(X)],
		[del(holding(X)), add(ontable(X)), add(clear(X)),
				  add(handempty)]).

move(stack(X, Y), [oneroom, holding(X), clear(Y)],
		[del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y)),
				  add(clear(X))]).

move(pickup(X), [tworoom, handempty, clear(X, Z), on(X, Y, Z), handroom(Z)],
		[del(handempty), del(clear(X, Z)), del(on(X, Y, Z)),
				 add(clear(Y, Z)),	add(holding(X))]).

move(pickup(X), [tworoom, handempty, clear(X, Z), ontable(X, Z), handroom(Z)],
		[del(handempty), del(clear(X, Z)), del(ontable(X, Z)),
				 add(holding(X))]).

move(putdown(X), [tworoom, holding(X), handroom(Z)],
		[del(holding(X)), add(ontable(X, Z)), add(clear(X, Z)),
				  add(handempty)]).

move(stack(X, Y), [tworoom, holding(X), clear(Y, Z), handroom(Z)],
		[del(holding(X)), del(clear(Y, Z)), add(handempty), add(on(X, Y, Z)),
				  add(clear(X, Z))]).

move(goroom1, 
        [handroom(2)],
        [del(handroom(2)), add(handroom(1))]).

move(goroom2,
        [handroom(1)],
        [del(handroom(1)), add(handroom(2))]).

/* run commands */

go(S, G) :- plan(S, G, [S], []).

test1 :- go([oneroom, handempty, ontable(b), ontable(c), on(a, b), clear(c), clear(a)],
	          [oneroom, handempty, ontable(c), on(a,b), on(b, c), clear(a)]).

test2 :- go([oneroom, handempty, ontable(b), ontable(c), on(a, b), clear(c), clear(a)],
	          [oneroom, handempty, ontable(a), ontable(b), on(c, b), clear(a), clear(c)]).

test3 :- go([tworoom, handempty, ontable(b,1), on(a, b, 1), clear(a, 1), handroom(1)],
            [tworoom, handempty, ontable(b,2), on(a, b, 2), clear(a, 2), handroom(1)]).

test4 :- go([oneroom, handempty, ontable(b), ontable(c), on(a, b), clear(a), clear(c)],
            [oneroom, handempty, ontable(c), on(b, c), on(a, b), clear(a)]).

test5 :- go([tworoom, handempty, ontable(b,1), ontable(c, 1), on(a, b, 1), clear(a, 1), clear(c, 1), handroom(1)],
            [tworoom, handempty, ontable(b,2), on(c, b, 2), on(a, c, 2), clear(a, 2), handroom(1)]).
