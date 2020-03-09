package com.gihutb.ryuseiishida.simulation.evacuation.environment;

import com.gihutb.ryuseiishida.simulation.evacuation.util.Parameter;

public class LoopController {
    private int countLoopNum = 0;

    public boolean isLoop() {
        return Parameter.LOOP_NUM > 0;
    }

    public boolean isResetLoop(int step) {
        if (step >= Parameter.END_STEP) {
            countLoopNum++;
            return true;
        }
        return false;
    }

    public boolean isEndLoop() {
        return countLoopNum >= Parameter.LOOP_NUM;
    }

    public int getCountLoopNum() {
        return countLoopNum;
    }
}
