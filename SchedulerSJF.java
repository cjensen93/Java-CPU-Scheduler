import java.util.LinkedList;

public class SchedulerSJF implements Scheduler {
    int contextSwitches = 0;
    int greatestTime = 0;
    LinkedList<Process> q = new LinkedList<>();
    Process current = null;
    Platform SJFPlatform;

    public SchedulerSJF(Platform platform) {
        SJFPlatform = platform;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        q.add(p);
        if (p.getTotalTime() > greatestTime) {
            greatestTime = p.getTotalTime();
        }
    }

    @Override
    public Process update(Process cpu) {

        // Set the initial current process to work on, so it's not null
        if (current == null) {
            current = q.peek();
        }

        // Create variable to return, and base number to compare SJF algorithm to
        Process p = null;
        int currentHighest = greatestTime + 1;

        // SJF algorithm to decide which process to work on next
        for (int i = 0; i < q.size(); i++) {

            // Determine what the lowest available total time process is
            if (!q.get(i).isExecutionComplete()) {
                if (q.get(i).getTotalTime() < currentHighest) {

                    // Set the return variable to this current process, and set the new lowest available time int
                    p = q.get(i);
                    currentHighest = p.getTotalTime();
                }
            }
        }

        // Return a message to see if current process burst is complete
        if (current != null) {
            if (current.isBurstComplete()) {
                SJFPlatform.log("Process " + current.getName() + " burst complete");
                contextSwitches++;
            }
        }

        // Return a message to see if current process execution is complete
        if (current != null) {
            if (current.isExecutionComplete()) {
                SJFPlatform.log("Process " + current.getName() + " execution complete");
                contextSwitches++;
            }
        }

        // If not the same, switch the current process to the one the algorithm selected next
        if (current != p) {
            if (p != null){
                current = p;
                SJFPlatform.log("Scheduled: " + current.getName());
            }
        }

        // Return the process the algorithm has picked
        return p;
    }
}
