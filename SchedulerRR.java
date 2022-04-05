import java.util.LinkedList;

public class SchedulerRR implements Scheduler {
    int contextSwitches = 0;
    int currentQueueStep = 0;
    int currentTimeQuanta = 0;
    int maxTimeQuanta;
    LinkedList<Process> q = new LinkedList<>();
    Process current = null;
    Platform RRPlatform;

    public SchedulerRR(Platform platform, int i) {
        RRPlatform = platform;
        maxTimeQuanta = i;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        q.add(p);
    }

    @Override
    public Process update(Process cpu) {
        // Set the initial current process to work on as the first item in the queue
        if (current == null) {
            current = q.peek();
            assert current != null;
            RRPlatform.log("Scheduled: " + current.getName());
            contextSwitches++;
        }

        // Check if all the work has been done, and return null if so
        int completeCount = 0;
        for (int i = 0;i < q.size(); i++) {
            if (q.get(i).isExecutionComplete())
                completeCount++;
        }
        if (completeCount == q.size()) {
            RRPlatform.log("Process " + current.getName() + " execution complete");
            contextSwitches++;
            return null;
        }

        // Checks if execution and is done, and needs to be changed to next process
        if (current.isExecutionComplete()) {
            RRPlatform.log("Process " + current.getName() + " execution complete");
            if (checkIfAvailable()) {
                return null;
            }
            switchStep();
            current = q.get(currentQueueStep);
            contextSwitches += 2;
            RRPlatform.log("Scheduled: " + current.getName());
        }

        // Checks if just the burst is done, and needs to be changed
        else if (currentTimeQuanta == maxTimeQuanta) {
            RRPlatform.log("Time quantum complete for process " + current.getName());
            switchStep();
            current = q.get(currentQueueStep);
            RRPlatform.log("Scheduled: " + current.getName());
            contextSwitches += 2;
        }

        currentTimeQuanta++;
        return current;
    }

    // Method to check what step to turn to next
    // This does the bulk of the algorithm checking in RR
    public void switchStep() {
        if (currentQueueStep == 0) {
            if (!q.get(1).isExecutionComplete()) {
                currentQueueStep = 1;
                currentTimeQuanta = 0;
            }
            else if (!q.get(2).isExecutionComplete()) {
                currentQueueStep = 2;
                currentTimeQuanta = 0;
            }
            else {
                currentTimeQuanta = 0;
            }
        }
        else if (currentQueueStep == 1) {
            if (!q.get(2).isExecutionComplete()) {
                currentQueueStep = 2;
                currentTimeQuanta = 0;
            }
            else if (!q.get(0).isExecutionComplete()) {
                currentQueueStep = 0;
                currentTimeQuanta = 0;
            }
            else {
                currentTimeQuanta = 0;
            }
        }
        else if (currentQueueStep == 2) {
            if (!q.get(0).isExecutionComplete()) {
                currentQueueStep = 0;
                currentTimeQuanta = 0;
            }
            else if (!q.get(1).isExecutionComplete()) {
                currentQueueStep = 1;
                currentTimeQuanta = 0;
            }
            else {
                currentTimeQuanta = 0;
            }
        }
    }

    // Method to see if any processes are available. Returns true if all processes are done
    public boolean checkIfAvailable() {
        int proceed = 0;
        for (int i = 0; i < q.size(); i++) {
            if (q.get(i).isExecutionComplete()) {
                proceed++;
            }
        }
        return proceed == q.size();
    }
}
