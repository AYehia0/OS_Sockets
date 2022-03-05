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
const testAndSet = (key) => {
  // atomic, you may ask ?
  let rv = LOCK 
  LOCK = true 
  return rv
}
const t1 = async () => {

  for (const process of processes) {

    //console.log(`Testing the lock: ${LOCK}`)
    while(testAndSet(LOCK));
    
    // perform the code
    console.log(`This process takes ${process.estimatedTime}ms`)

    await wait(process.estimatedTime)

    // release the lock
    LOCK = false 

  }
}

t1()
