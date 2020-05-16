package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.github.ryuseiishida.pedestrian_simulation.agent.Agent;
import com.github.ryuseiishida.pedestrian_simulation.agent.StateTag;
import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class LoopController {
    private int countLoopNum = 0;
    private Environment env;

    public LoopController(Environment environment) {
        this.env = environment;
    }

    public void newEnvironment(Environment environment) {
        this.env = environment;
    }

    public boolean isResetLoop(int step) {
        if (Parameter.END.equals("ESCAPED") && env.getStep() != 0) {
            int goalAgentCounter = 0;
            for (Agent agent : env.getAgentList()) {
                if (agent.getStateTag().equals(StateTag.moveGoal)) {
                    goalAgentCounter++;
                }
            }
            if (goalAgentCounter == 0) {
                countLoopNum++;
                return true;
            }
        } else if (Parameter.END.equals("END_STEP") && step > Parameter.END_STEP && Parameter.END_STEP != 0) {
            countLoopNum++;
            return true;
        }
        return false;
    }

    public boolean isEndLoop() {
        if (countLoopNum >= Parameter.LOOP_NUM && Parameter.LOOP_NUM != 0) {
            return true;
        }
        return false;
    }

    public int getCountLoopNum() {
        return countLoopNum;
    }
}
