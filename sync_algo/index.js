const MAX_PROCESSES = 10

class Process {
  // each process has ID, estimatedTime (ms), size (byte)
  constructor() {
    // TODO : doesn't guarantee a unique id
    this.id = Date.now() + Math.floor(Math.random() * 100)
    this.size = Math.floor(Math.random() * 100 + 1)
    this.estimatedTime = Math.floor(Math.random() * 10 + 1)
  }
}

const wait = (ms) => {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve();
    }, ms * 1000)
  })
}

// generate some processes
const generateProcesses = () => {
  const processes = []
  for(let p = 0; p <= MAX_PROCESSES; p++) {
    processes.push(new Process())
  }
  return processes
}
const processes = generateProcesses()

// test and set
let LOCK = false
const testAndSet = () => {
  // atomic, you may ask ?
  let rv = LOCK 
  LOCK = true 
  return rv
}
const t1 = async () => {

  for (const process of processes) {

    //console.log(`Testing the lock: ${LOCK}`)
    while(testAndSet());
    
    // perform the code
    console.log(`This process takes ${process.estimatedTime}ms`)

    await wait(process.estimatedTime)

    // release the lock
    LOCK = false 
  }
}

// wait and signal
// it's basically the same as semaphore
// so ...

// creating the semaphore
class Semaphore {
  // the val is the max number of process to pass
  constructor (val) {
    this.val = val
    this.queue = [] 
  }
}

// 0, 1, 2
const MAX_VALUE = 3
const sem = new Semaphore(MAX_VALUE)

// asigns the semaphore to a process
const wait_ = async (semaphore, process) => {

  semaphore.val--

  if (semaphore.val < 0){
    semaphore.queue.push(process)

    // Put the process in the suspend/waiting list  
    //await wait(process.estimatedTime)
    console.log(`BLOCKED: process ${JSON.stringify(process)} has been blocked`)
  }else {
    console.log(`CS: Semaphore assigned to process ${JSON.stringify(process)}, it takes ${process.estimatedTime}ms`)
  }
}

// informs other processess that the Semaphore is released
const signal = async (semaphore) => {
  
  semaphore.val++

  if (semaphore.val <= 0){
    // remove it from the waiting list
    // remove the first came
    let p = semaphore.queue.shift()

    // wake it up
    if (p)
      console.log(`Process ${JSON.stringify(p)} is up`)
    //await wait(process.estimatedTime)
  }
}

const t2 = () => {

  console.log(processes)

  console.log(sem)
  wait_(sem, processes[0])
  wait_(sem, processes[1])
  wait_(sem, processes[2])
  wait_(sem, processes[3])

  console.log(sem)

  // do stuff
  // assume process 0 finishes and wants to leave
  signal(sem)

  console.log(sem)
 }

//t1()

t2()
