/*
 This is a simple implementation for the seek strategies 
 used in the Device Handler to search for sectors/data

 Algorithms : 
  - FCFS (First Come First Served)
  - SSTF (Shortest Seek Time First)
  - SCAN :
    - LOOK, N-Step SCAN, C-SCAN, and C-LOOK
 */

// request queue && head pos are required.

const FCFS = (requestsQueue, headPos) => {

  let totalSeekTime = 0
  requestsQueue.forEach(req => {

    totalSeekTime += Math.abs(req - headPos)

    headPos = req

  })
  return totalSeekTime
}

const SSTF= (requestsQueue, headPos) => {

  let totalSeekTime = 0

  let tempQueue = requestsQueue

  while (tempQueue.length > 0) {
    // find the shortest req
    let shortest = tempQueue.sort((a, b) => {
      return Math.abs(headPos - a) - Math.abs(headPos - b);
    })[0]

    totalSeekTime += Math.abs(shortest - headPos)

    headPos = shortest

    // remove from the list
    tempQueue = tempQueue.filter(req => {
      return req !== shortest
    })
  }
  return totalSeekTime
}

// direction 1 : right
// direction -1 : left
// default is left
const SCAN= (requestsQueue, headPos, direction=-1) => {

  let totalSeekTime = 0
  const MAX_SIZE = Math.max(...requestsQueue) + 20

  if (direction === -1)
    requestsQueue.push(0)
  else 
    requestsQueue.push(MAX_SIZE)

  let tempQueue = requestsQueue.sort((a, b) => {
    return a - b
  })

  while (tempQueue.length > 0) {
    // find the shortest req
    let shortest = 0 
    if (direction === -1){
      shortest = tempQueue.sort((a, b) => {
        return Math.abs(headPos - a) - Math.abs(headPos - b);
      })[0]

    }else {
      // not shortest anymore, largest
      let x = tempQueue.sort((a, b) => {
        return Math.abs(headPos - a) - Math.abs(headPos - b);
      }) 

      if (x.length !== 1)
        shortest = x[1]
      else 
        shortest = x[0]
    }
    totalSeekTime += Math.abs(shortest - headPos)

    headPos = shortest

    // remove from the list
    tempQueue = tempQueue.filter(req => {
      return req !== shortest
    })

  }
  return totalSeekTime
}


let seq = [176, 79, 34, 60, 92, 11, 41, 114]
let head = 50
 
let ans1 = FCFS(seq, head)
let ans2 = SSTF(seq, head)
let ans3 = SCAN(seq, head)

console.log(`FCFS seek time : ${ans1}`)
console.log(`SSTF seek time : ${ans2}`)
console.log(`SCAN seek time : ${ans3}`)
