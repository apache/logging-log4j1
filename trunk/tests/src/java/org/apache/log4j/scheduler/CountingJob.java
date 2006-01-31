package org.apache.log4j.scheduler;

class CountingJob implements Job {
  int count = 0;
  int id;
  long desiredTime;
  boolean deleted;

  CountingJob(int id, long scheduledTime) {
    this.id = id;
    this.desiredTime = scheduledTime;
  }

  public void execute() {
    if (deleted) {
      throw new IllegalStateException(id + "has already been deleted");
    }

    long now = System.currentTimeMillis();
    count++;

    if (now < desiredTime) {
      throw new IllegalStateException("Job executed too early.");
    } else if ((now - desiredTime) > SchedulerTest.TOLERATED_GAP) {
      String msg =
        "Job id " + id + " executed " + (now - desiredTime) + " too late ";
      System.out.println(msg);
      throw new IllegalStateException(msg);
    }
  }

  void markAsDeleted() {
    deleted = true;
  }
  void sanityCheck(long currentTime) {
  }
}

