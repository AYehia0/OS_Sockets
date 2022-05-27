=begin
Task Scheduling algorithms : 
    - Least Slack Time (LST)
	- Earliest Deadline First (EDF)
	- Rate Monotonic scheduling (RMS)
=end

# LST
# https://www.geeksforgeeks.org/least-slack-time-lst-scheduling-algorithm-in-real-time-systems/
# the algorithm works be calculating the slack time, in order to decide what task to be exectuted.
=begin
It is different from the Earliest Deadline First because it requires execution times of the task which are to be scheduled. Hence it is sometimes impractical to implement the Least Slack Time scheduling algorithm because the burst time of the tasks in real-time systems is difficult to predict
=end

# Max time limit, you know it's not going to take for ever (realtime bois)
MAX_TIME = 12

# assuming all the tasks came at the same time
# the deadline = duration for simplicity
class Task
  # To keep track of the current task, we have to know :
  # the consumed time, the time left for the deadline/release
  # by default  : 
      # the consumed time at start is 0
      # time to deadline = arrival time + the current deadline
      # time to release = arrival time

  def initialize(id, arrival_time=0, deadline=nil, duration, execute_time)
    @id = id
    @arrival_time = arrival_time
    @duration = duration
    @exec_time = execute_time
    @deadline = duration unless deadline

    # globals
    @time_to_release = @arrival_time
    @time_to_deadline = @arrival_time + @deadline
    @consumed_time = 0
  end

  # print the object as a pretty string
  def to_s
    "(ID=#{@id}, ARIV_TIME=#{@arival_time}, PERIOD=#{@duration}, EXEC_TIME=#{@exec_time}, DEADLINE=#{@deadline})"
  end

  attr_accessor :time_to_release , :time_to_deadline , :consumed_time
  attr_accessor :duration , :exec_time , :arrival_time, :deadline, :id
end


class LST
  @@time_now = 0
  def initialize(step=0.5, tasks)
    @tasks = tasks
    @time_step = step

    self.move(init=true)
  end

  # check if the task it scheduleble
  # task is scheduleble if it's arrival_time doesn't exceed the MAX_TIME
  # and its consumed time less than the exectue time
  def is_sched(task)
    task.arrival_time <= @@time_now && task.consumed_time < task.exec_time
  end

  # calculating the slack time
  def slack_time(task)
    task.time_to_deadline - (task.exec_time - task.consumed_time)
  end

  # calculate the slack time for all the tasks
  # choose the task with the minimum slack time
  def choose_task
    tasks_slacks = []
    @tasks.each do |task|
      # check if the task it scheduleble
      if is_sched(task) 
        # calculate the slack time 
        tasks_slacks << {
          task: task,
          slack_time: slack_time(task)
        }
      end
    end

    if tasks_slacks.length == 0
      p "Empty Slack Times"
      return nil
    end

    # find the minimum time in the list
    return tasks_slacks.min_by{ |task| task[:slack_time] }
  end

  # travel in time, get to the next time slot
  def move(init=false)

    if not init
      @@time_now += @time_step
    end

    @tasks.each do |task| 
      if not init and task.time_to_deadline > 0
        #p task.time_to_deadline 
        task.time_to_deadline -= @time_step
      elsif not init and task.consumed_time != task.exec_time
        p "#{task.id} broken deadline"
      end

      if task.time_to_release > 0
        task.time_to_release -= @time_step
      else
        task.consumed_time = 0
        task.time_to_release = task.duration
        task.time_to_deadline = task.deadline
      end
    end
  end

  # the actual scheduling process
  def run

    while @@time_now < MAX_TIME

      # get the least slack time
      choosen_task = choose_task()
      if choosen_task != nil
        p "MIN SLACK TIME : #{choosen_task[:slack_time]}" 

        choosen_task[:task].consumed_time += @time_step

        p "T#{choosen_task[:task].id} : [#{@@time_now}]"
      end

      # travel
      move
    end
  end
end

tasks = [
  Task.new(id=1, duration=4, execute_time=1.5),
  Task.new(id=2, duration=10, execute_time=3),
  Task.new(id=3, duration=12, execute_time=3),
]

sched = LST.new(tasks)
puts sched.run