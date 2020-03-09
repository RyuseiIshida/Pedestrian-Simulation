package com.gihub.ryuseiishida.simulation.evacuation.agent;

public class StateTag {
    /** the state of the agent is initial or nothing*/
    public static final String none = "none";

    /** the state of the agent is move goal*/
    public static final String moveGoal = "moveGoal";

    /** the state of the agent is follow move*/
    public static final String follow = "follow";

    /** the state of the agent is random move */
    public static final String randomWalk = "randomWalk";
}
