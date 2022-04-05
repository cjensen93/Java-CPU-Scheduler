import java.util.LinkedList;

public class SchedulerSRTF implements Scheduler {
    int contextSwitches = 0;
    int greatestTime = 0;
    LinkedList<Process> q = new LinkedList<>();
    Process current = null;
    Platform SRTFPlatform;

    public SchedulerSRTF(Platform platform) {
        SRTFPlatform = platform;
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
            assert current != null;
            SRTFPlatform.log("Scheduled: " + current.getName());
            contextSwitches++;
        }

        // Create variable to return, and base number to compare SRTF algorithm to
        Process p = null;
        int currentLowest = greatestTime + 1;

        // SRTF algorithm to decide which process to work on next
        for (int i = 0; i < q.size(); i++) {

            // Determine what the lowest available remaining time process is
            if (!q.get(i).isExecutionComplete()) {
                if (q.get(i).getRemainingBurst() < currentLowest) {

                    // Set the return variable to this current process, and set the new lowest available time int
                    p = q.get(i);
                    currentLowest = p.getRemainingBurst();
                }
            }
        }

        // Return a message to see if current process burst is complete
        if (current != null) {
            if (current.isBurstComplete()) {
                SRTFPlatform.log("Process " + current.getName() + " burst complete");
                contextSwitches++;
            }
        }

        // Return a message to see if current process execution is complete
        if (current != null) {
            if (current.isExecutionComplete()) {
                SRTFPlatform.log("Process " + current.getName() + " execution complete");
                contextSwitches++;
            }
        }

        // If not the same, switch the current process to the one the algorithm selected next
        if (current != p) {
            if (p != null){
                if (contextSwitches == 1) {
                    assert current != null;
                    SRTFPlatform.log("Preemptively removed: " + current.getName());
                    contextSwitches++;
                }
                current = p;
                SRTFPlatform.log("Scheduled: " + current.getName());
            }
        }

        // Return the process the algorithm has picked
        return p;
    }
}
