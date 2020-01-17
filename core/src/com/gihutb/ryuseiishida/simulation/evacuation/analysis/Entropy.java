package com.gihutb.ryuseiishida.simulation.evacuation.analysis;

import com.gihutb.ryuseiishida.simulation.evacuation.Parameter;
import com.gihutb.ryuseiishida.simulation.evacuation.agent.Agent;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.Cell;
import com.gihutb.ryuseiishida.simulation.evacuation.cell.CellsMap;

import java.util.ArrayList;

public class Entropy {
    public static float calcEntropy(ArrayList<Agent> agentList, CellsMap cellsMap) {
        float total = 0;
        for (Cell cell : cellsMap.getCells()) {
            float agentSize = 0;
            for (Agent agent : agentList) {
                agentSize += cell.equals(cellsMap.getCell(agent.getPosition())) ? 1 : 0;
            }
            //System.out.println("agentSize = " + agentSize);
            if(agentSize != 0) {
                total += (agentSize / agentList.size()) * log2(agentSize / agentList.size());
            }
        }
        return total * -1;
    }

    private static float log2(float value) {
        return (float) (Math.log(value) / Math.log(2.0));
    }
}
