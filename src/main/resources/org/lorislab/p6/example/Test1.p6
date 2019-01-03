{
  "processId": "org.lorislab.p6.example.Test1",
  "processVersion": "1.0.0",
  "nodes": [
    {
      "START_EVENT": {
        "name": "start"
      }
    },
    {
      "SERVICE_TASK": {
        "name": "service1"
      }
    },
    {
      "PARALLEL_GATEWAY": {
        "name": "gateway1",
        "sequenceFlow": "DIVERGING"
      }
    },
    {
      "SERVICE_TASK": {
        "name": "service3"
      }
    },
    {
      "SERVICE_TASK": {
        "name": "service4"
      }
    },
    {
      "PARALLEL_GATEWAY": {
        "name": "gateway2",
        "sequenceFlow": "CONVERGING"
      }
    },
    {
      "END_EVENT": {
        "name": "end"
      }
    }
  ],
  "sequence": {
    "gateway2": {
      "from": [
        "service3",
        "service4"
      ],
      "to": [
        "end"
      ]
    },
    "gateway1": {
      "from": [
        "service1"
      ],
      "to": [
        "service3",
        "service4"
      ]
    },
    "start": {
      "to": [
        "service1"
      ]
    },
    "service1": {
      "from": [
        "start"
      ],
      "to": [
        "gateway1"
      ]
    },
    "service4": {
      "from": [
        "gateway1"
      ],
      "to": [
        "gateway2"
      ]
    },
    "end": {
      "from": [
        "gateway2"
      ]
    },
    "service3": {
      "from": [
        "gateway1"
      ],
      "to": [
        "gateway2"
      ]
    }
  }
}