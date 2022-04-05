import java.util.LinkedList;

public class SchedulerPriority implements Scheduler {
    int contextSwitches = 0;
    int totalBurstTime = 0;
    LinkedList<Process> q = new LinkedList<>();
    Process current = null;
    Platform priorityPlatform;

    public SchedulerPriority(Platform platform) {
        priorityPlatform = platform;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        q.add(p);
        totalBurstTime += p.getTotalTime();
    }

    @Override
    public Process update(Process cpu) {

        // Base case to see if there's any more work to do
        if (totalBurstTime < 0) {
            return null;
        }

        // Set the current process to work on as the first item in the queue
        if (current == null) {
            current = q.peek();
        }

        // Create variable to return, and base number to compare Priority algorithm to
        Process p = null;
        int lowestAvailable = q.size() + 1;

        // Priority algorithm to decide which process to work on next
        for (int i = 0; i < q.size(); i++) {

            // Determine what the lowest available priority process is
            if (q.get(i).getPriority() < lowestAvailable) {
                if (!q.get(i).isBurstComplete()) {

                    // Set the return variable to this current process, and set the new lowest available priority int
                    p = q.get(i);
                    lowestAvailable = p.getPriority();
                }
            }
        }

        // Return a message to see if current process burst is complete
        if (current != null) {
            if (current.isBurstComplete()) {
                priorityPlatform.log("Process " + current.getName() + " burst complete");
                contextSwitches++;
            }
        }

        // Return a message to see if current process execution is complete
        if (current != null) {
            if (current.isExecutionComplete()) {
                priorityPlatform.log("Process " + current.getName() + " execution complete");
                contextSwitches++;
            }
        }

        // If not the same, switch the current process to the one the algorithm selected next
        if (current != p) {
            if (p != null){
                current = p;
                priorityPlatform.log("Scheduled: " + current.getName());
            }
        }

        // Decrement total burst time, and return the process
        totalBurstTime--;
        return p;
    }
}
