import java.util.LinkedList;

public class SchedulerFCFS implements Scheduler {
    int contextSwitches = 0;
    int currentQueueStep = 0;
    LinkedList<Process> q = new LinkedList<>();
    Process current = null;
    Platform FCFSPlatform;

    public SchedulerFCFS(Platform platform) {
        FCFSPlatform = platform;
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
            FCFSPlatform.log("Scheduled: " + current.getName());
        }

        // Check if all the work has been done, and return null if so
        int completeCount = 0;
        for (int i = 0;i < q.size(); i++) {
            if (q.get(i).isExecutionComplete())
                completeCount++;
        }
        if (completeCount == q.size()) {
            FCFSPlatform.log("Process " + current.getName() + " burst complete");
            FCFSPlatform.log("Process " + current.getName() + " execution complete");
            return null;
        }

        // Checks if both execution and burst is done, and needs to be changed
        if (current.isExecutionComplete()) {
            if (current.isBurstComplete()) {
                FCFSPlatform.log("Process " + current.getName() + " burst complete");
                FCFSPlatform.log("Process " + current.getName() + " execution complete");
                switchStep();
                current = q.get(currentQueueStep);
                contextSwitches += 3;
                FCFSPlatform.log("Scheduled: " + current.getName());
            }
        }

        // Checks if just the burst is done, and needs to be changed
        else if (current.isBurstComplete()) {
            FCFSPlatform.log("Process " + current.getName() + " burst complete");
            switchStep();
            current = q.get(currentQueueStep);
            contextSwitches += 2;
            FCFSPlatform.log("Scheduled: " + current.getName());
        }

        return current;
    }

    public void switchStep() {
        if (currentQueueStep == 0) {
            currentQueueStep = 1;
        }
        else if (currentQueueStep == 1) {
            currentQueueStep = 2;
        }
        else if (currentQueueStep == 2) {
            currentQueueStep = 0;
        }
    }
}

