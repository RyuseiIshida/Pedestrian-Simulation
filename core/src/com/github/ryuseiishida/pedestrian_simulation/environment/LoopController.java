package com.github.ryuseiishida.pedestrian_simulation.environment;

import com.github.ryuseiishida.pedestrian_simulation.util.Parameter;

public class LoopController {
    private int countLoopNum = 0;

    public boolean isResetLoop(int step) {
        if (step >= Parameter.END_STEP && Parameter.END_STEP != 0) {
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
