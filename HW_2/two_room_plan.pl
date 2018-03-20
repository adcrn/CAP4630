% University of Central Florida
% CAP 4630 - Spring 2018
% Author(s): Nhi Nguyen and Alexander deCurnou

:- module(planner,
    [
        plan/4, test1/0, test2/0, go/2, goroom1/0, goroom2/0,
        move/3, change_state/3, conditions_met/2, member_state/2
    ]).

:- [utils].

plan(State, Goal, _, Moves) :- equal_set(State, Goal),
                write('Moves are:'), nl,
                reverse_print_stack(Moves).
